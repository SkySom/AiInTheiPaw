package io.sommers.aiintheipaw.twitch.message;

import io.sommers.aiintheipaw.core.messagebuilder.MessageBuilder;
import io.sommers.aiintheipaw.core.user.IUser;
import io.sommers.aiintheipaw.core.util.ProviderId;
import io.sommers.aiintheipaw.twitch.TwitchConfiguration;
import io.sommers.aiintheipaw.twitch.user.TwitchUserProvider;

public class TwitchMessageBuilder extends MessageBuilder {
    private final TwitchConfiguration twitchConfiguration;
    private final TwitchUserProvider twitchUserProvider;

    public TwitchMessageBuilder(TwitchConfiguration twitchConfiguration, TwitchUserProvider twitchUserProvider) {
        super();
        this.twitchConfiguration = twitchConfiguration;
        this.twitchUserProvider = twitchUserProvider;
    }

    @Override
    public MessageBuilder withCommandArg(String commandName) {
        return this.addArg(this.twitchConfiguration.getCommandPrefix() + commandName);
    }

    public MessageBuilder withUserArg(ProviderId providerId) {
        this.addArg(this.twitchUserProvider.getUser(providerId.id())
                .blockOptional()
                .map(IUser::getName)
                .orElse("Nobody")
        );
        return this;
    }

    @Override
    public MessageBuilder withUserArg(IUser user) {
        return this;
    }
}
