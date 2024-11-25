package io.sommers.ai.twitch.model;

import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.model.IMessage;
import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.messagebuilder.MessageBuilder;
import io.sommers.ai.twitch.TwitchConstants;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

public class TwitchChannel implements IChannel {
    private final TwitchMessageService twitchMessageService;
    private final ProviderId broadcasterId;

    public TwitchChannel(TwitchMessageService twitchMessageService, String broadcasterId) {
        this.twitchMessageService = twitchMessageService;
        this.broadcasterId = new ProviderId(TwitchConstants.PROVIDER, broadcasterId);
    }

    @Override
    public Mono<String> sendMessage(IMessage message) {
        return this.twitchMessageService.sendToChannel(
                this,
                message
        );
    }

    @Override
    public Mono<String> sendMessage(Function<MessageBuilder, MessageBuilder> messageBuilder) {
        return this.twitchMessageService.sendToChannel(this, messageBuilder.apply(this.twitchMessageService.getMessageBuilder()));
    }

    @Override
    public ProviderId getId() {
        return this.broadcasterId;
    }
}
