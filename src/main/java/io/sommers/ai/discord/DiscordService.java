package io.sommers.ai.discord;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class DiscordService implements DisposableBean, InitializingBean {
    private final DiscordConfiguration discordConfiguration;

    private GatewayDiscordClient discordClient;

    public DiscordService(DiscordConfiguration discordConfiguration) {
        this.discordConfiguration = discordConfiguration;
    }

    @Override
    public void afterPropertiesSet() {
        this.discordClient = DiscordClient.builder(discordConfiguration.getToken())
                .build()
                .gateway()
                .login()
                .block();

        if (this.discordClient == null) {
            throw new IllegalStateException("Failed to Initialize Discord Service");
        }
    }

    @Override
    public void destroy() {
        if (this.discordClient != null) {
            this.discordClient.logout()
                    .block();
        }
    }
}
