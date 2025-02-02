package io.sommers.aiintheipaw.twitch.user;

import io.sommers.aiintheipaw.core.user.IUser;
import io.sommers.aiintheipaw.core.user.IUserProvider;
import io.sommers.aiintheipaw.core.user.NamedUser;
import io.sommers.aiintheipaw.twitch.TwitchConstants;
import io.sommers.aiintheipaw.twitch.TwitchService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
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
                .stream()
                .findFirst()
        ).map(user -> new NamedUser(this.createId(user.getId()), user.getDisplayName()));
    }

    @Override
    public String getProvider() {
        return TwitchConstants.PROVIDER;
    }
}
