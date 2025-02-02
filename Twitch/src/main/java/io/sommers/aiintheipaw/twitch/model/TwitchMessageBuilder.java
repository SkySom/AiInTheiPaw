package io.sommers.aiintheipaw.twitch.model;

import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.messagebuilder.MessageBuilder;
import io.sommers.ai.model.user.IUser;
import io.sommers.aiintheipaw.twitch.TwitchConfiguration;

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
