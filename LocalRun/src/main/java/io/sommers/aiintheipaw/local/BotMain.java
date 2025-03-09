package io.sommers.aiintheipaw.local;

import io.sommers.aiintheipaw.commands.CommandConfigurationReference;
import io.sommers.aiintheipaw.core.CoreConfigurationReference;
import io.sommers.aiintheipaw.twitch.TwitchConfigurationReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableR2dbcRepositories
@EnableCaching
@ConfigurationPropertiesScan("io.sommers.aiintheipaw")
@Import({
        CoreConfigurationReference.class,
        CommandConfigurationReference.class,
        TwitchConfigurationReference.class
})
public class BotMain {
    public static void main(String[] args) {
        SpringApplication.run(BotMain.class, args);
    }
}
