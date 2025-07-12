package org.example.worldcast.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
    private String name = "Unknown";
    private double lat = 0.0;
    private double lon = 0.0;
}
