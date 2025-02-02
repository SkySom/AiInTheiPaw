package io.sommers.aiintheipaw.twitch.model;

import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.model.message.IMessage;
import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.messagebuilder.MessageBuilder;
import io.sommers.aiintheipaw.twitch.TwitchConstants;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

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
    public Mono<String> sendMessage(@Nullable String replyTo, Function<MessageBuilder, MessageBuilder> messageBuilder) {
        return this.twitchMessageService.sendToChannel(this, messageBuilder.apply(this.twitchMessageService.getMessageBuilder()), replyTo);
    }

    @Override
    public ProviderId getId() {
        return this.broadcasterId;
    }
}
