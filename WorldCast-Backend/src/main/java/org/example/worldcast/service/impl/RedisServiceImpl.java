package org.example.worldcast.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.worldcast.ApplicationProperties;
import org.example.worldcast.domain.dto.common.Location;
import org.example.worldcast.domain.dto.response.WeatherResponse;
import org.example.worldcast.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationProperties properties;
    private final ObjectMapper objectMapper;
    // 5 minutes

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate, ApplicationProperties properties, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public WeatherResponse  getWeatherFromCache(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location must not be null");
        }

        String cacheKey = buildCacheKey(properties.getCache().getPrefix(), location);

        Object cachedData = redisTemplate.opsForValue().get(cacheKey);

        try {
            return objectMapper.convertValue(cachedData, WeatherResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert cached data to WeatherResponse for key: " + cacheKey, e);
        }
    }

    @Override
    public void cacheWeatherData(Location location, WeatherResponse weatherResponse) {
        String cacheKey = buildCacheKey(properties.getCache().getPrefix(), location);
        redisTemplate.opsForValue().set(cacheKey, weatherResponse, properties.getCache().getTtl(), TimeUnit.SECONDS);
    }

    // Round up latitude and longitude to 2 decimal for avoding cache key of nearby locations.
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