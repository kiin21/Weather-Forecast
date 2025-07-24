package org.example.worldcast.client;

import org.example.worldcast.domain.dto.response.TomorrowIOWeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "tomorrow-io", url = "https://api.tomorrow.io/v4")
public interface TomorrowIOClient {

    @GetMapping("/weather/forecast")
    TomorrowIOWeatherResponse getWeatherForecast(
            @RequestParam("location") String location, @RequestParam("apikey") String apiKey);
}
