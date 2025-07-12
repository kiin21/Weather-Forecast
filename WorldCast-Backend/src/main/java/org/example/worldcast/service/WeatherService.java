package org.example.worldcast.service;

import org.example.worldcast.domain.dto.response.WeatherResponse;

public interface WeatherService {

    WeatherResponse getWeatherByLocation(String location);
}
