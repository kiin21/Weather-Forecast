package org.example.worldcast.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpenStreetMapResponse {

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("lat")
    private String lat;

    @JsonProperty("lon")
    private String lon;
}
