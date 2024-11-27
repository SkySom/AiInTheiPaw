package io.sommers.ai.model.channel;

import io.sommers.ai.model.message.IMessage;
import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.messagebuilder.MessageBuilder;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface IChannel {
    Mono<String> sendMessage(IMessage message);

    default Mono<String> sendMessage(Function<MessageBuilder, MessageBuilder> messageBuilder) {
        return this.sendMessage(null, messageBuilder);
    }

    Mono<String> sendMessage(@Nullable String replyTo, Function<MessageBuilder, MessageBuilder> messageBuilder);

    ProviderId getId();
}
