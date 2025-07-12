package org.example.worldcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients(basePackages = "org.example.worldcast.client")
@EnableConfigurationProperties
@EnableAsync
public class WorldCastApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorldCastApplication.class, args);
    }

}
