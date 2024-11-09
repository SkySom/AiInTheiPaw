package io.sommers.ai;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotMain.class);

    public static void main(String[] args) {
        Config config = ConfigFactory.load();

        Config discordConfig = config.getConfig("discord");
        GatewayDiscordClient gatewayDiscordClient = DiscordClient.builder(discordConfig.getString("token"))
                .build()
                .gateway()
                .login()
                .block();

        if (gatewayDiscordClient != null) {
            


            gatewayDiscordClient.onDisconnect()
                    .block();
        } else {
            LOGGER.error("Failed to Login Discord Client");
        }

    }
}
