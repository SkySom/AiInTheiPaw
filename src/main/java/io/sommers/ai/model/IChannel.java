package io.sommers.ai.model;

import reactor.core.publisher.Mono;

public interface IChannel {
    Mono<String> replyTo(IMessage message);

    String getId();

    String getService();
}
