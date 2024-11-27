package io.sommers.ai.twitch.model;

import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.user.IUser;
import io.sommers.ai.model.user.NamedUser;
import io.sommers.ai.model.user.User;
import io.sommers.ai.provider.IUserProvider;
import io.sommers.ai.twitch.TwitchConstants;
import io.sommers.ai.twitch.TwitchService;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

import java.util.List;

public class TwitchUserProvider implements IUserProvider {
    private final TwitchService twitchService;

    public TwitchUserProvider(TwitchService twitchService) {
        this.twitchService = twitchService;
    }

    @Override
    public Mono<IUser> getUser(String userId) {
        return Mono.justOrEmpty(twitchService.getTwitchClient()
                .getUsers(null, List.of(userId), null)
                .execute()
                .getUsers()
                .getFirst()
        ).map(user -> new NamedUser(this.createId(user.getId()), user.getDisplayName()));
    }

    @Override
    public String getProvider() {
        return TwitchConstants.PROVIDER;
    }
}
