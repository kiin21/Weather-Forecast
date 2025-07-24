package org.example.worldcast.service.impl;

import org.example.worldcast.domain.dto.common.CurrentWeather;
import org.example.worldcast.domain.dto.common.ForecastWeather;
import org.example.worldcast.domain.dto.common.Location;
import org.example.worldcast.domain.dto.response.WeatherResponse;
import org.example.worldcast.service.WeatherService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service("mockWeatherService")
public class MockWeatherServiceImpl implements WeatherService {

    @Override
    public WeatherResponse getWeatherByLocation(String location) {
        Location mockLocation =
                Location.builder()
                        .name("An Khanh, Ho Chi Minh City, Vietnam")
                        .lat(10.1569393)
                        .lon(106.2658137)
                        .build();

        CurrentWeather currentWeather =
                CurrentWeather.builder()
                        .temperature(26.1)
                        .humidity(91)
                        .pressure(1008)
                        .windSpeed(2.5)
                        .windDirection(228)
                        .weatherDescription(
                                "It's currently a warm and very humid day with skies mostly covered by clouds. You won't need an umbrella, as there's no rain expected, and winds are light.")
                        .icon("cloudy")
                        .timestamp(Instant.parse("2025-07-11T16:08:00Z"))
                        .build();

        List<ForecastWeather> forecast =
                Arrays.asList(
                        ForecastWeather.builder()
                                .date(LocalDate.parse("2025-07-10"))
                                .minTemperature(25.1)
                                .maxTemperature(30.7)
                                .weatherDescription(
                                        "On July 10, 2025, expect a hot and very humid day with temperatures reaching up to 30.7°C, though it will feel as warm as 36.9°C. Cloudy conditions are anticipated, bringing a good chance of rain with potential for moderate downpours and an accumulation of over 10 mm. Despite the clouds, the UV index will be very high at times, so sun protection is advised.")
                                .icon("cloudy")
                                .build(),
                        ForecastWeather.builder()
                                .date(LocalDate.parse("2025-07-11"))
                                .minTemperature(25.0)
                                .maxTemperature(30.6)
                                .weatherDescription(
                                        "Expect very hot and muggy conditions on July 11, 2025, with apparent temperatures reaching up to 37.8°C due to high humidity. The day will see a mix of clear and cloudy skies, alongside a chance of moderate rainfall. Be aware of a very high UV index throughout the day.")
                                .icon("cloudy")
                                .build(),
                        ForecastWeather.builder()
                                .date(LocalDate.parse("2025-07-12"))
                                .minTemperature(24.9)
                                .maxTemperature(30.4)
                                .weatherDescription(
                                        "For July 12, 2025, expect a largely cloudy day with temperatures ranging from a comfortable 24.9°C to a warm 30.4°C, though it may feel as hot as 37.3°C. There's a 40% chance of rain, with accumulated rainfall potentially reaching 16.2 mm and peak intensity at 8.38 mm, accompanied by light breezes averaging 2.7 m/s and gusts up to 9.4 m/s.")
                                .icon("cloudy")
                                .build(),
                        ForecastWeather.builder()
                                .date(LocalDate.parse("2025-07-13"))
                                .minTemperature(24.5)
                                .maxTemperature(29.8)
                                .weatherDescription(
                                        "For July 13, 2025, expect a cloudy day with high humidity, as cloud cover will average 94% and humidity will be around 91%. Temperatures will range from 24.5°C to 29.8°C, feeling as warm as 36.0°C, with a chance of light rain accumulating to about 15.69mm and average wind speeds of 3.0 m/s. The UV index will be moderate, peaking at 7.")
                                .icon("cloudy")
                                .build(),
                        ForecastWeather.builder()
                                .date(LocalDate.parse("2025-07-14"))
                                .minTemperature(24.6)
                                .maxTemperature(28.4)
                                .weatherDescription(
                                        "On July 14, 2025, expect a warm and very humid day with temperatures ranging from 24.6°C to 28.4°C, though it will feel much warmer, potentially up to 33.4°C. The skies will be mostly cloudy to overcast, bringing a moderate chance of light rain showers, with some periods of heavier rainfall possible. A light to moderate breeze will be present throughout the day.")
                                .icon("cloudy")
                                .build(),
                        ForecastWeather.builder()
                                .date(LocalDate.parse("2025-07-15"))
                                .minTemperature(25.1)
                                .maxTemperature(30.8)
                                .weatherDescription(
                                        "On July 15, 2025, expect a warm day with temperatures ranging from 25.1°C to 30.8°C, feeling as warm as 37.9°C at its peak. The skies will be mostly cloudy, with cloud cover averaging 79%, and there's a moderate chance of rain, with an average intensity of 0.48 mm/hr and a total accumulation of 5.76 mm. Light breezes averaging 2.6 m/s, with gusts up to 9.1 m/s, will be present.")
                                .icon("cloudy")
                                .build());

        return WeatherResponse.builder()
                .location(mockLocation)
                .current(currentWeather)
                .forecast(forecast)
                .build();
    }
}
