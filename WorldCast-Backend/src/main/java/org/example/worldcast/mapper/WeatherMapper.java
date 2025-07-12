package org.example.worldcast.mapper;

import org.example.worldcast.domain.dto.common.CurrentWeather;
import org.example.worldcast.domain.dto.common.ForecastWeather;
import org.example.worldcast.domain.dto.response.TomorrowIOWeatherResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class WeatherMapper {

    public CurrentWeather mapToCurrentWeather(TomorrowIOWeatherResponse response) {
        if (response.getTimelines() == null ||
                response.getTimelines().getHourly() == null ||
                response.getTimelines().getHourly().isEmpty()) {
            throw new RuntimeException("No current weather data available");
        }

        // Use the first minutely forecast as current weather
        TomorrowIOWeatherResponse.MinuteForecast firstHourly = response.getTimelines().getMinutely().getFirst();
        TomorrowIOWeatherResponse.MinuteValues values = firstHourly.getValues();

        CurrentWeather current = new CurrentWeather();
        current.setTemperature(values.getTemperature());
        current.setHumidity(values.getHumidity());
        current.setPressure((int) values.getPressureSeaLevel());
        current.setWindSpeed(values.getWindSpeed());
        current.setWindDirection(values.getWindDirection());
        current.setIcon(getWeatherIcon(values.getWeatherCode()));
        current.setTimestamp(Instant.parse(firstHourly.getTime()));

        return current;
    }

    public List<ForecastWeather> mapToForecastWeather(TomorrowIOWeatherResponse response) {
        List<ForecastWeather> forecastList = new ArrayList<>();

        if (response.getTimelines() == null ||
                response.getTimelines().getDaily() == null) {
            return forecastList;
        }

        for (TomorrowIOWeatherResponse.DailyForecast dailyForecast : response.getTimelines().getDaily()) {
            TomorrowIOWeatherResponse.DailyValues values = dailyForecast.getValues();

            ForecastWeather forecast = new ForecastWeather();
            forecast.setDate(LocalDate.parse(dailyForecast.getTime().substring(0, 10)));
            forecast.setMinTemperature(values.getTemperatureMin());
            forecast.setMaxTemperature(values.getTemperatureMax());
            forecast.setIcon(getWeatherIcon(values.getWeatherCodeMax()));

            forecastList.add(forecast);
        }

        return forecastList;
    }

    public String getWeatherIcon(int weatherCode) {
        return switch (weatherCode) {
            case 1000 -> "clear-day";
            case 1001, 1102 -> "cloudy";
            case 1100, 1101 -> "partly-cloudy-day";
            case 2000, 2100 -> "fog";
            case 4000, 4001, 4200, 4201 -> "rain";
            case 5000, 5001, 5100, 5101 -> "snow";
            case 6000, 6001, 6200, 6201, 7000, 7101, 7102 -> "sleet";
            case 8000 -> "thunderstorm";
            default -> "unknown";
        };
    }
}