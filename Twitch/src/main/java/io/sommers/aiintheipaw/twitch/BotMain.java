package io.sommers.aiintheipaw.twitch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableScheduling
@ConfigurationPropertiesScan("io.sommers.aiintheipaw")
public class BotMain {
    public static void main(String[] args) {
        SpringApplication.run(BotMain.class, args);
    }
}
