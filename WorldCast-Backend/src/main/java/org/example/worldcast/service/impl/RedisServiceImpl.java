package org.example.worldcast.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.worldcast.ApplicationProperties;
import org.example.worldcast.domain.dto.common.Location;
import org.example.worldcast.domain.dto.response.WeatherResponse;
import org.example.worldcast.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationProperties properties;
    private final ObjectMapper objectMapper;

    public RedisServiceImpl(
            RedisTemplate<String, Object> redisTemplate,
            ApplicationProperties properties,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public WeatherResponse getWeatherFromCache(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location must not be null");
        }

        String cacheKey = buildCacheKey(properties.getCache().getPrefix(), location);

        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData == null) {
                return null;
            }

            return objectMapper.convertValue(cachedData, WeatherResponse.class);
        } catch (Exception e) {
            log.error(
                    "Failed to get weather data from Redis cache for key: {}. Error: {}",
                    cacheKey,
                    e.getMessage());
            throw new RuntimeException("Failed to get cached data for key: " + cacheKey, e);
        }
    }

    @Override
    public void cacheWeatherData(Location location, WeatherResponse weatherResponse) {
        String cacheKey = buildCacheKey(properties.getCache().getPrefix(), location);

        try {
            redisTemplate
                    .opsForValue()
                    .set(cacheKey, weatherResponse, properties.getCache().getTtl(), TimeUnit.SECONDS);
            log.debug("Successfully cached weather data for key: {}", cacheKey);
        } catch (Exception e) {
            log.error(
                    "Failed to cache weather data to Redis for key: {}. Error: {}", cacheKey, e.getMessage());
            throw new RuntimeException("Failed to cache weather data for key: " + cacheKey, e);
        }
    }

    // Round up latitude and longitude to 2 decimal for avoding cache key of nearby
    // locations.
    private String buildCacheKey(String prefix, Location location) {
        if (location == null || !StringUtils.hasText(location.getName())) {
            throw new IllegalArgumentException("Location must have a valid name");
        }

        // round up 2 decimal places for latitude and longitude
        Double roundedLatitude = Math.round(location.getLat() * 100.0) / 100.0;
        Double roundedLongitude = Math.round(location.getLon() * 100.0) / 100.0;

        return String.format("%s:lat=%s_lon=%s", prefix, roundedLatitude, roundedLongitude);
    }
}
