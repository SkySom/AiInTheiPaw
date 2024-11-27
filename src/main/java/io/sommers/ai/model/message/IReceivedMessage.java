package io.sommers.ai.model.message;

import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.model.messagebuilder.MessageBuilder;
import io.sommers.ai.model.user.IUser;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface IReceivedMessage extends IMessage {
    ProviderId getId();

    IChannel getChannel();

    Mono<String> replyTo(Function<MessageBuilder, MessageBuilder> messageBuilderFunction);

    IUser getUser();
}
