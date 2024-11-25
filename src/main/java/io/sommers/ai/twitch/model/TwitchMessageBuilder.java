package io.sommers.ai.twitch.model;

import io.sommers.ai.model.IUser;
import io.sommers.ai.model.messagebuilder.MessageBuilder;
import io.sommers.ai.twitch.TwitchConfiguration;

public class TwitchMessageBuilder extends MessageBuilder {
    private final TwitchConfiguration twitchConfiguration;

    public TwitchMessageBuilder(TwitchConfiguration twitchConfiguration) {
        super();
        this.twitchConfiguration = twitchConfiguration;
    }

    @Override
    public MessageBuilder withCommandArg(String commandName) {
        return this.addArg(this.twitchConfiguration.getCommandPrefix() + commandName);
    }

    @Override
    public MessageBuilder withUserArg(IUser user) {
        return this;
    }

    @Override
    public MessageBuilder withMessageArg(MessageBuilder subMessage) {
        return this;
    }
}
