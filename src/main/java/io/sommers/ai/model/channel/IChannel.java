package io.sommers.ai.model.channel;

import io.sommers.ai.model.IMessage;
import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.messagebuilder.MessageBuilder;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface IChannel {
    Mono<String> sendMessage(IMessage message);

    Mono<String> sendMessage(Function<MessageBuilder, MessageBuilder> messageBuilder);

    ProviderId getId();
}
