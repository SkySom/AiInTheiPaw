package io.sommers.ai.service;

import io.sommers.ai.model.message.IMessage;
import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.model.messagebuilder.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

public interface IMessageService {

    Mono<String> sendToChannel(IChannel channel, IMessage message);

    Mono<String> sendToChannel(IChannel channel, IMessage message, @Nullable String replyTo);

    Mono<String> sendToChannel(IChannel channel, @NotNull MessageBuilder messageBuilder);

    Mono<String> sendToChannel(IChannel channel, @NotNull MessageBuilder messageBuilder, @Nullable String replyTo);

    MessageBuilder getMessageBuilder();
}
