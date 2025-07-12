package org.example.worldcast.domain.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.example.worldcast.domain.dto.common.CurrentWeather;
import org.example.worldcast.domain.dto.common.ForecastWeather;
import org.example.worldcast.domain.dto.common.Location;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private Location location;
    private CurrentWeather current;
    private List<ForecastWeather> forecast;
}