package io.sommers.ai.twitch;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "twitch")
public class TwitchConfiguration {
    @NotEmpty
    private final String clientId;
    @NotEmpty
    private final String clientSecret;
    @NotEmpty
    private final String oauthSecret;
    @NotEmpty
    private final String eventSubSecret;
    @NotEmpty
    private final String eventCallbackDomain;
    @NotEmpty
    private final String commandPrefix;
    @Nullable
    private final String twitchURL;
    @NotEmpty
    private final String botId;

    public TwitchConfiguration(String clientId, String clientSecret, String oauthSecret, String eventSubSecret, String eventCallbackDomain, String commandPrefix, @Nullable String twitchURL, String botId) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.oauthSecret = oauthSecret;
        this.eventSubSecret = eventSubSecret;
        this.eventCallbackDomain = eventCallbackDomain;
        this.commandPrefix = commandPrefix;
        this.twitchURL = twitchURL;
        this.botId = botId;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public String getOauthSecret() {
        return this.oauthSecret;
    }

    public String getEventSubSecret() {
        return this.eventSubSecret;
    }

    public String getEventCallbackDomain() {
        return this.eventCallbackDomain;
    }

    @Nullable
    public String getTwitchURL() {
        return this.twitchURL;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }

    public String getBotId() {
        return botId;
    }
}
