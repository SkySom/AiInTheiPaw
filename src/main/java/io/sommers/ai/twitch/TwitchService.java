package io.sommers.ai.twitch;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.TwitchHelixBuilder;
import org.springframework.beans.factory.InitializingBean;

public class TwitchService implements InitializingBean {
    private final TwitchConfiguration twitchConfiguration;

    private TwitchHelix twitchClient;

    public TwitchService(TwitchConfiguration twitchConfiguration) {
        this.twitchConfiguration = twitchConfiguration;
    }

    @Override
    public void afterPropertiesSet() {
        TwitchHelixBuilder twitchHelixBuilder = TwitchHelixBuilder.builder()
                .withClientId(this.twitchConfiguration.getClientId())
                .withClientSecret(this.twitchConfiguration.getClientSecret());

        if (this.twitchConfiguration.getTwitchURL() != null) {
            twitchHelixBuilder = twitchHelixBuilder.withBaseUrl(this.twitchConfiguration.getTwitchURL());
        }

        this.twitchClient = twitchHelixBuilder.build();
    }

    public TwitchHelix getTwitchClient() {
        return twitchClient;
    }
}
