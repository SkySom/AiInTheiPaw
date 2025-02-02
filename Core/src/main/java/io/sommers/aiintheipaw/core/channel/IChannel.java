package io.sommers.aiintheipaw.core.channel;

import io.sommers.aiintheipaw.core.util.ProviderId;
import io.sommers.aiintheipaw.core.message.IMessage;
import io.sommers.aiintheipaw.core.messagebuilder.MessageBuilder;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.util.function.Function;

public interface IChannel {
    Mono<String> sendMessage(IMessage message);

    default Mono<String> sendMessage(Function<MessageBuilder, MessageBuilder> messageBuilder) {
        return this.sendMessage(null, messageBuilder);
    }

    Mono<String> sendMessage(@Nullable String replyTo, Function<MessageBuilder, MessageBuilder> messageBuilder);

    ProviderId getId();
}
