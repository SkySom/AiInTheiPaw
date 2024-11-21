package io.sommers.ai.service;

import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.model.IMessage;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

public interface IMessageService {

    Mono<String> sendToChannel(IChannel channel, IMessage message);

    Mono<String> sendToChannel(IChannel channel, IMessage message, @Nullable String replyTo);
}
