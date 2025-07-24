package org.example.worldcast.service;

import org.example.worldcast.domain.dto.common.Location;

public interface GeoService {

    Location getLocation(String locationName);

    Location getLocation(double latitude, double longitude);
}
