package io.sommers.ai.discord;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConditionalOnProperty(prefix = "discord", name = "enabled", havingValue = "true")
public class DiscordBeans {

    @Bean
    public DiscordService getDiscordService(DiscordConfiguration discordConfiguration) {
        return new DiscordService(discordConfiguration);
    }
}
