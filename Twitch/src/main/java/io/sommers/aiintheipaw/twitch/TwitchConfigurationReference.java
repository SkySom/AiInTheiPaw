package io.sommers.aiintheipaw.twitch;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableCaching
@EnableWebFlux
@ComponentScan("io.sommers.aiintheipaw.twitch")
public class TwitchConfigurationReference {
}
