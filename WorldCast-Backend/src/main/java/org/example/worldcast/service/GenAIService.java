package org.example.worldcast.service;

import org.example.worldcast.domain.dto.common.Location;
import org.example.worldcast.domain.dto.response.TomorrowIOWeatherResponse;

import java.util.concurrent.CompletableFuture;

public interface GenAIService {
    String generateWeatherSummary(TomorrowIOWeatherResponse.MinuteForecast minuteForecast);

    String generateWeatherSummary(TomorrowIOWeatherResponse.DailyForecast dailyForecast);

    CompletableFuture<Void> generateAndCacheSummary(Location location, TomorrowIOWeatherResponse originalResponse);

    String chatWithAI(String message);
}
