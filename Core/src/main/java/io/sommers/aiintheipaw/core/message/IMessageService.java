package io.sommers.aiintheipaw.core.message;

import io.sommers.aiintheipaw.core.channel.IChannel;
import io.sommers.aiintheipaw.core.messagebuilder.MessageBuilder;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

public interface IMessageService {

    Mono<String> sendToChannel(IChannel channel, IMessage message);

    Mono<String> sendToChannel(IChannel channel, IMessage message, @Nullable String replyTo);

    Mono<String> sendToChannel(IChannel channel, @NotNull MessageBuilder messageBuilder);

    Mono<String> sendToChannel(IChannel channel, @NotNull MessageBuilder messageBuilder, @Nullable String replyTo);

    MessageBuilder getMessageBuilder();
}
