package io.sommers.aiintheipaw.twitch.message;

import io.sommers.aiintheipaw.core.messagebuilder.MessageBuilder;
import io.sommers.aiintheipaw.core.user.IUserSourceInfo;
import io.sommers.aiintheipaw.core.util.ProviderId;
import io.sommers.aiintheipaw.twitch.TwitchConfiguration;
import io.sommers.aiintheipaw.twitch.user.TwitchUserSourceProvider;

public class TwitchMessageBuilder extends MessageBuilder {
    private final TwitchConfiguration twitchConfiguration;
    private final TwitchUserSourceProvider twitchUserProvider;

    public TwitchMessageBuilder(TwitchConfiguration twitchConfiguration, TwitchUserSourceProvider twitchUserProvider) {
        super();
        this.twitchConfiguration = twitchConfiguration;
        this.twitchUserProvider = twitchUserProvider;
    }

    @Override
    public MessageBuilder withCommandArg(String commandName) {
        return this.addArg(this.twitchConfiguration.getCommandPrefix() + commandName);
    }

    public MessageBuilder withUserArg(ProviderId providerId) {
        this.addArg(this.twitchUserProvider.getUserSource(providerId.id())
                .blockOptional()
                .map(IUserSourceInfo::getName)
                .orElse("Nobody")
        );
        return this;
    }

    @Override
    public MessageBuilder withUserArg(IUserSourceInfo user) {
        return this;
    }
}
