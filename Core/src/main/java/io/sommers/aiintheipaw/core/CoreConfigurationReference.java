package io.sommers.aiintheipaw.core;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
@EnableCaching
@ComponentScan("io.sommers.aiintheipaw.core")
public class CoreConfigurationReference {
}
