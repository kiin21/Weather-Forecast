package org.example.worldcast.service.impl;

import org.example.worldcast.client.OpenStreetMapClient;
import org.example.worldcast.domain.dto.common.Location;
import org.example.worldcast.domain.dto.response.OpenStreetMapResponse;
import org.example.worldcast.service.GeoService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeoServiceImpl implements GeoService {

    private final OpenStreetMapClient client;

    public GeoServiceImpl(OpenStreetMapClient client) {
        this.client = client;
    }

    @Tool(description = "Fetches location by name")
    @Override
    public Location getLocation(String locationName) {
        List<OpenStreetMapResponse> responses = client.getLocationName("json", locationName, 1, 1);

        if (responses.isEmpty()) {
            throw new RuntimeException("No location found for: " + locationName);
        }

        OpenStreetMapResponse response = responses.getFirst();

        return new Location(
                response.getDisplayName(),
                Double.parseDouble(response.getLat()),
                Double.parseDouble(response.getLon()));
    }

    @Tool(description = "Fetches location by latitude and longitude")
    @Override
    public Location getLocation(double latitude, double longitude) {
        OpenStreetMapResponse response = client.getReverseLocation("json", latitude, longitude, 1);

        if (response == null || response.getDisplayName() == null) {
            throw new RuntimeException(
                    "No location found for coordinates: " + latitude + ", " + longitude);
        }

        return new Location(
                response.getDisplayName(),
                Double.parseDouble(response.getLat()),
                Double.parseDouble(response.getLon()));
    }
}
