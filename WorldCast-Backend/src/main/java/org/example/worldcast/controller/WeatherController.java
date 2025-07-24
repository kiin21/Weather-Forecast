package org.example.worldcast.controller;

import jakarta.validation.constraints.NotBlank;
import org.example.worldcast.domain.dto.response.WeatherResponse;
import org.example.worldcast.service.WeatherService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(@Qualifier("weatherService") WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(
            @RequestParam @NotBlank(message = "Location cannot be blank") String location) {
        WeatherResponse response = weatherService.getWeatherByLocation(location.trim());
        return ResponseEntity.ok(response);
    }
}
