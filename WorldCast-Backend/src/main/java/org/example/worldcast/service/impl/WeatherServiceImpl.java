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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("weatherService")
public class WeatherServiceImpl implements WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);

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
            RedisService redisService,
            GenAIService genAIService,
            ApplicationProperties applicationProperties) {
        this.tomorrowIOClient = tomorrowIOClient;
        this.geoService = geoService;
        this.weatherMapper = weatherMapper;
        this.redisService = redisService;
        this.genAIService = genAIService;
        this.applicationProperties = applicationProperties;
    }

    @Tool(description = "Fetches weather data by location name or coordinates")
    @Override
    @Cacheable(value = "weather-cache", key = "#location", unless = "#result == null")
    public WeatherResponse getWeatherByLocation(String location) {
        try {
            Location parsedLocation = parseLocation(location);
            logger.info("Cache miss for location: {}, fetching from API", parsedLocation.toString());
            return fetchWeatherFromAPI(parsedLocation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch weather data: " + e.getMessage(), e);
        }
    }

    @CacheEvict(value = "weather-cache", key = "#location")
    public void evictCache(String location) {
        logger.info("Cache evicted for location: {}", location);
    }

    @CacheEvict(value = "weather-cache", allEntries = true)
    public void evictAllCache() {
        logger.info("All weather cache evicted");
    }

    private WeatherResponse fetchWeatherFromAPI(Location location) {
        String locationParam = location.getLat() + "," + location.getLon();

        try {
            // Call weather API
            TomorrowIOWeatherResponse response =
                    tomorrowIOClient.getWeatherForecast(locationParam, applicationProperties.getApiKey());

            WeatherResponse weatherResponse =
                    WeatherResponse.builder()
                            .location(location)
                            .current(weatherMapper.mapToCurrentWeather(response))
                            .forecast(weatherMapper.mapToForecastWeather(response))
                            .build();

            // Async: Generate AI descriptions
            genAIService.generateAndCacheAIDescriptions(location, response, weatherResponse);

            return weatherResponse;
        } catch (Exception e) {
            logger.error("Error fetching weather data from tomorrowIO for location {}: {}", location, e.getMessage());
            throw new RuntimeException("Error on calling tomorrowIO API: " + e.getMessage(), e);
        }
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
