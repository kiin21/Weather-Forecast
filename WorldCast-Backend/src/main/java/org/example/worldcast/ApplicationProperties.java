package org.example.worldcast;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "weather")
public class ApplicationProperties {
    private String apiKey;
    private Cache cache = new Cache();

    @Getter
    @Setter
    public static class Cache {
        private String prefix;
        private long ttl;
    }
}