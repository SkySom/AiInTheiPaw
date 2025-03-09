package io.sommers.aiintheipaw.twitch.user;

import io.sommers.aiintheipaw.core.user.IUserSourceInfo;
import io.sommers.aiintheipaw.core.user.IUserSourceProvider;
import io.sommers.aiintheipaw.core.user.NamedUserSourceInfo;
import io.sommers.aiintheipaw.twitch.TwitchConstants;
import io.sommers.aiintheipaw.twitch.TwitchService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TwitchUserSourceProvider implements IUserSourceProvider {
    private final TwitchService twitchService;

    public TwitchUserSourceProvider(TwitchService twitchService) {
        this.twitchService = twitchService;
    }

    @Override
    public Mono<IUserSourceInfo> getUserSource(String userId) {
        return Mono.justOrEmpty(twitchService.getTwitchClient()
                .getUsers(null, List.of(userId), null)
                .execute()
                .getUsers()
                .stream()
                .findFirst()
        ).map(user -> new NamedUserSourceInfo(this.createId(user.getId()), user.getDisplayName()));
    }

    @Override
    public String getProvider() {
        return TwitchConstants.PROVIDER;
    }
}
