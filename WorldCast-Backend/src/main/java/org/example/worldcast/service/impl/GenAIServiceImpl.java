package org.example.worldcast.service.impl;

import org.example.worldcast.domain.dto.common.ForecastWeather;
import org.example.worldcast.domain.dto.common.Location;
import org.example.worldcast.domain.dto.response.TomorrowIOWeatherResponse;
import org.example.worldcast.domain.dto.response.WeatherResponse;
import org.example.worldcast.service.GenAIService;
import org.example.worldcast.service.RedisService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class GenAIServiceImpl implements GenAIService {

    private final ChatClient chatClient;
    private final RedisService redisService;
    private final String systemMessage = """
            You are a helpful AI assistant that generates weather summaries based on provided data.
            Your responses should be concise, friendly, and easy to understand.
            Always use the date provided in the data for daily forecasts, never mention the day of the week.
            """;

    // Template for minute forecast
    private static final String MINUTE_PROMPT_TEMPLATE = """
            Generate a concise and easy-to-understand weather summary for the following minute forecast:
            
            Data: {weatherforecast}
            
            Please write 1-2 sentences describing the current weather in a natural and friendly way.
            """;

    // Template for daily forecast
    private static final String DAILY_PROMPT_TEMPLATE = """
            Generate a weather summary for the following day:
            
            Data: {weatherforecast}
            
            Please write 2-3 sentences describing the weather for the day in a vivid and helpful way, never include the day, use the date provided in data instead.
            """;


    public GenAIServiceImpl(ChatClient.Builder builder, RedisService redisService) {
        this.chatClient = builder.build();
        this.redisService = redisService;
    }

    @Override
    public String generateWeatherSummary(TomorrowIOWeatherResponse.MinuteForecast minuteForecast) {
        try {
            // Create prompt template
            PromptTemplate promptTemplate = new PromptTemplate(MINUTE_PROMPT_TEMPLATE);

            // Prepare data for template - pass entire forecast object
            Map<String, Object> promptVariables = Map.of("weatherforecast", minuteForecast.toString()
            );

            // Create prompt from template
            Prompt prompt = promptTemplate.create(promptVariables).augmentSystemMessage(systemMessage);

            // Get content from response
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
            // Create prompt template
            PromptTemplate promptTemplate = new PromptTemplate(DAILY_PROMPT_TEMPLATE);

            // Prepare data for template - pass entire forecast object
            Map<String, Object> promptVariables = Map.of("weatherforecast", dailyForecast.toString()
            );

            // Create prompt from template
            Prompt prompt = promptTemplate.create(promptVariables).augmentSystemMessage(systemMessage);

            // Call AI model
            return chatClient.prompt(prompt).call().content();

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Async
    public CompletableFuture<Void> generateAndCacheSummary(Location location, TomorrowIOWeatherResponse originalResponse) {
        try {
            // Get fresh data from cache
            WeatherResponse cachedResponse = redisService.getWeatherFromCache(location);
            if (cachedResponse == null) {
                System.err.println("No cached data found for key: " + location.toString());
                return CompletableFuture.completedFuture(null);
            }

            // Generate AI descriptions
            String currentDesc = this.generateWeatherSummary(originalResponse.getTimelines().getMinutely().getFirst());
            cachedResponse.getCurrent().setWeatherDescription(currentDesc);

            for (TomorrowIOWeatherResponse.DailyForecast forecast : originalResponse.getTimelines().getDaily()) {
                String desc = this.generateWeatherSummary(forecast);

                ForecastWeather forecastWeather = cachedResponse.getForecast().stream()
                        .filter(f -> f.getDate().toString().equals(forecast.getTime().substring(0, 10)))
                        .findFirst()
                        .orElse(null);

                if (forecastWeather != null) {
                    forecastWeather.setWeatherDescription(desc);
                } else {
                    System.err.println("No matching forecast found for date: " + forecast.getTime().substring(0, 10));
                }
            }

            // Update cache with AI descriptions
            redisService.cacheWeatherData(location, cachedResponse);

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            System.err.println("Error in async AI processing: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public String chatWithAI(String message) {
        Prompt prompt = new Prompt(new SystemMessage("You are Khoa's latest AI model"), new UserMessage(message));

        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}