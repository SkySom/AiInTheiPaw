package io.sommers.ai.model.service;

import io.sommers.ai.model.IChannel;
import io.sommers.ai.model.IMessage;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

public interface IMessageService {

    Mono<String> sendToChannel(IChannel channel, IMessage message);

    Mono<String> sendToChannel(IChannel channel, IMessage message, @Nullable String replyTo);
}
