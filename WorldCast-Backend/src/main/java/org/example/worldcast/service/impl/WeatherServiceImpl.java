package org.example.worldcast.service.impl;

import org.example.worldcast.ApplicationProperties;
import org.example.worldcast.client.TomorrowIOClient;
import org.example.worldcast.domain.dto.common.Location;
import org.example.worldcast.domain.dto.response.TomorrowIOWeatherResponse;
import org.example.worldcast.domain.dto.response.WeatherResponse;
import org.example.worldcast.mapper.WeatherMapper;
import org.example.worldcast.service.GenAIService;
import org.example.worldcast.service.GeoService;
import org.example.worldcast.service.RedisService;
import org.example.worldcast.service.WeatherService;
import org.springframework.stereotype.Service;

@Service("weatherService")
public class WeatherServiceImpl implements WeatherService {
    private final TomorrowIOClient tomorrowIOClient;

    private final GeoService geoService;
    private final RedisService redisService;
    private final GenAIService genAIService;

    private final WeatherMapper weatherMapper;
    private final ApplicationProperties applicationProperties;


    public WeatherServiceImpl(
            TomorrowIOClient tomorrowIOClient,
            GeoService geoService,
            WeatherMapper weatherMapper,
            RedisService redisService, GenAIService genAIService, ApplicationProperties applicationProperties) {
        this.tomorrowIOClient = tomorrowIOClient;
        this.geoService = geoService;
        this.weatherMapper = weatherMapper;
        this.redisService = redisService;
        this.genAIService = genAIService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public WeatherResponse getWeatherByLocation(String location) {
        try {
            Location parsedLocation = parseLocation(location);
            // Check cache first
            WeatherResponse cachedResponse = redisService.getWeatherFromCache(parsedLocation);
            if (cachedResponse != null) {
                return cachedResponse;
            }

            // Cache miss - fetch from API and cache the result
            System.out.println("Cache miss for location: " + parsedLocation.toString());
            return fetchAndCacheWeatherFromAPI(parsedLocation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch weather data: " + e.getMessage(), e);
        }
    }

    private WeatherResponse fetchAndCacheWeatherFromAPI(Location location) {
        String locationParam = location.getLat() + "," + location.getLon();

        // Call weather API
        TomorrowIOWeatherResponse response = tomorrowIOClient.getWeatherForecast(locationParam, applicationProperties.getApiKey());

        WeatherResponse weatherResponse = WeatherResponse.builder()
                .location(location)
                .current(weatherMapper.mapToCurrentWeather(response))
                .forecast(weatherMapper.mapToForecastWeather(response))
                .build();

        // Cache immediately with basic data
        redisService.cacheWeatherData(location, weatherResponse);

        // Async: Generate AI descriptions and update cache
        genAIService.generateAndCacheSummary(location, response);

        // Return immediately (without AI descriptions)
        return weatherResponse;
    }


    private Location parseLocation(String location) {
        // Check if the location string is in "lat,lon" format
        if (location.matches("^-?\\d+(\\.\\d+)?\\s*,\\s*-?\\d+(\\.\\d+)?$")) {
            String[] coords = location.split(",");
            double latitude = Double.parseDouble(coords[0].trim());
            double longitude = Double.parseDouble(coords[1].trim());
            return geoService.getLocation(latitude, longitude);
        } else {
            return geoService.getLocation(location);
        }
    }
}