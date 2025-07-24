package org.example.worldcast.service;

import org.example.worldcast.domain.dto.common.Location;
import org.example.worldcast.domain.dto.response.WeatherResponse;

public interface RedisService {

    WeatherResponse getWeatherFromCache(Location location);

    void cacheWeatherData(Location location, WeatherResponse weatherResponse);
}
