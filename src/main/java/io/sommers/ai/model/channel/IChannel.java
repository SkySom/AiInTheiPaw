package io.sommers.ai.model.channel;

import io.sommers.ai.model.IMessage;
import io.sommers.ai.model.ProviderId;
import reactor.core.publisher.Mono;

public interface IChannel {
    Mono<String> sendMessage(IMessage message);

    ProviderId getId();
}
