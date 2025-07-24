package org.example.worldcast.service.impl;

import org.example.worldcast.domain.dto.common.ForecastWeather;
import org.example.worldcast.domain.dto.common.Location;
import org.example.worldcast.domain.dto.response.TomorrowIOWeatherResponse;
import org.example.worldcast.domain.dto.response.WeatherResponse;
import org.example.worldcast.service.GenAIService;
import org.example.worldcast.service.RedisService;
import org.example.worldcast.service.WeatherService;
import org.example.worldcast.utils.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class GenAIServiceImpl implements GenAIService {

    // Template for minute forecast
    private static final String MINUTE_PROMPT_TEMPLATE =
            """
                    Generate a concise and easy-to-understand weather summary for the following minute forecast:
                    
                    Data: {weatherforecast}
                    
                    Please write 1-2 sentences describing the current weather in a natural and friendly way.
                    """;
    // Template for daily forecast
    private static final String DAILY_PROMPT_TEMPLATE =
            """
                    Generate a weather summary for the following day:
                    
                    Data: {weatherforecast}
                    
                    Please write 2-3 sentences describing the weather for the day in a vivid and helpful way, never include the day, use the date provided in data instead.
                    """;
    private final ChatClient chatClient;
    private final RedisService redisService;
    private final String systemMessage =
            """
                    You are a helpful AI assistant that generates weather summaries based on provided data.
                    Your responses should be concise, friendly, and easy to understand.
                    Always use the date provided in the data for daily forecasts, never mention the day of the week.
                    """;
    @Autowired
    private ApplicationContext applicationContext;

    public GenAIServiceImpl(ChatClient.Builder builder, RedisService redisService) {
        this.chatClient = builder.build();
        this.redisService = redisService;
    }

    @Override
    public String generateWeatherSummary(TomorrowIOWeatherResponse.MinuteForecast minuteForecast) {
        try {
            PromptTemplate promptTemplate = new PromptTemplate(MINUTE_PROMPT_TEMPLATE);
            Map<String, Object> promptVariables = Map.of("weatherforecast", minuteForecast.toString());
            Prompt prompt = promptTemplate.create(promptVariables).augmentSystemMessage(systemMessage);
            return chatClient.prompt(prompt).call().content();

        } catch (Exception e) {
            // Log error and return fallback message
            System.err.println("Error generating minute forecast summary: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String generateWeatherSummary(TomorrowIOWeatherResponse.DailyForecast dailyForecast) {
        try {
            PromptTemplate promptTemplate = new PromptTemplate(DAILY_PROMPT_TEMPLATE);
            Map<String, Object> promptVariables = Map.of("weatherforecast", dailyForecast.toString());
            Prompt prompt = promptTemplate.create(promptVariables).augmentSystemMessage(systemMessage);

            return chatClient.prompt(prompt).call().content();

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Async
    @CachePut(value = "weather-cache", key = "#location.lat + ',' + #location.lon")
    public CompletableFuture<WeatherResponse> generateAndCacheAIDescriptions(
            Location location,
            TomorrowIOWeatherResponse originalResponse,
            WeatherResponse weatherResponse) {
        try {
            // Generate current weather description
            String currentDesc =
                    this.generateWeatherSummary(originalResponse.getTimelines().getMinutely().getFirst());
            weatherResponse.getCurrent().setWeatherDescription(currentDesc);

            // Generate forecast descriptions
            for (TomorrowIOWeatherResponse.DailyForecast forecast :
                    originalResponse.getTimelines().getDaily()) {
                String desc = this.generateWeatherSummary(forecast);

                ForecastWeather forecastWeather =
                        weatherResponse.getForecast().stream()
                                .filter(f -> f.getDate().toString().equals(forecast.getTime().substring(0, 10)))
                                .findFirst()
                                .orElse(null);

                if (forecastWeather != null) {
                    forecastWeather.setWeatherDescription(desc);
                }
            }

            return CompletableFuture.completedFuture(weatherResponse);

        } catch (Exception e) {
            System.err.println("Error in async AI processing: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public String chatWithAI(String message) {
        Prompt prompt =
                new Prompt(new SystemMessage(
                        "You are Khoa's latest AI model. When users ask about weather information, " +
                                "you MUST use the available weather tools to get current data. " +
                                "Never say you don't have access to real-time data - use the tools provided."
                ), new UserMessage(message));

        return chatClient.prompt(prompt).call().content();
    }

    @Override
    public Flux<String> chatWithAIStream(String message) {
        Prompt prompt =
                new Prompt(
                        new SystemMessage(
                                "You are Khoa's latest AI model. You can help with weather information by calling weather tools when needed."),
                        new UserMessage(message));

        WeatherService weatherService = applicationContext.getBean(WeatherService.class);

        ToolCallback[] tools = ToolCallbacks.from(weatherService, new DateTimeTools());

        return chatClient.prompt(prompt).toolCallbacks(tools).stream().content();
    }
}
