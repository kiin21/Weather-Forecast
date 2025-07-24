package org.example.worldcast.service;

import org.example.worldcast.domain.dto.common.Location;
import org.example.worldcast.domain.dto.response.TomorrowIOWeatherResponse;
import org.example.worldcast.domain.dto.response.WeatherResponse;
import reactor.core.publisher.Flux;

import java.util.concurrent.CompletableFuture;

public interface GenAIService {
    String generateWeatherSummary(TomorrowIOWeatherResponse.MinuteForecast minuteForecast);

    String generateWeatherSummary(TomorrowIOWeatherResponse.DailyForecast dailyForecast);

    String chatWithAI(String message);

    Flux<String> chatWithAIStream(String message);

    CompletableFuture<WeatherResponse> generateAndCacheAIDescriptions(
            Location location,
            TomorrowIOWeatherResponse originalResponse,
            WeatherResponse weatherResponse);
}
