package org.example.worldcast.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentWeather {
    private double temperature;
    private int humidity;
    private int pressure;
    private double windSpeed;
    private int windDirection;
    private String weatherDescription;
    private String icon;
    private Instant timestamp;
}
