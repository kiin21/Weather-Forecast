package org.example.worldcast.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForecastWeather {
    private LocalDate date;
    private double minTemperature;
    private double maxTemperature;
    private String weatherDescription;
    private String icon;
}