package io.sommers.ai.discord;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties("discord")
public class DiscordConfiguration {
    private final String token;

    @ConstructorBinding
    public DiscordConfiguration(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
