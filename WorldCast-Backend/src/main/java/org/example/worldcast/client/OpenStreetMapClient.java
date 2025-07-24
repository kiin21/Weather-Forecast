package org.example.worldcast.client;

import org.example.worldcast.domain.dto.response.OpenStreetMapResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "openstreetmap", url = "https://nominatim.openstreetmap.org")
public interface OpenStreetMapClient {

    @GetMapping("/search")
    List<OpenStreetMapResponse> getLocationName(
            @RequestParam("format") String format,
            @RequestParam("q") String query,
            @RequestParam("limit") int limit,
            @RequestParam("addressdetails") int addressDetails);

    @GetMapping("/reverse")
    OpenStreetMapResponse getReverseLocation(
            @RequestParam("format") String format,
            @RequestParam("lat") double latitude,
            @RequestParam("lon") double longitude,
            @RequestParam("addressdetails") int addressDetails);
}
