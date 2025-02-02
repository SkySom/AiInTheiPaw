package io.sommers.aiintheipaw.twitch;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.TwitchHelixBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class TwitchService implements InitializingBean {
    private final TwitchConfiguration twitchConfiguration;

    private TwitchHelix twitchClient;
    private CredentialManager credentialManager;
    private TwitchIdentityProvider identityProvider;

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

        this.credentialManager = CredentialManagerBuilder.builder()
                .build();

        this.identityProvider = new TwitchIdentityProvider(
                this.twitchConfiguration.getClientId(),
                this.twitchConfiguration.getClientSecret(),
                this.twitchConfiguration.getEventCallbackDomain() + "/twitch/oauth"
        );

        this.credentialManager.registerIdentityProvider(this.identityProvider);
    }

    @SuppressWarnings("unused")
    public CredentialManager getCredentialManager() {
        return credentialManager;
    }

    public TwitchIdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public TwitchHelix getTwitchClient() {
        return twitchClient;
    }
}
