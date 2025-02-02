package io.sommers.aiintheipaw.core.message;



import io.sommers.aiintheipaw.core.util.ProviderId;
import io.sommers.aiintheipaw.core.channel.IChannel;
import io.sommers.aiintheipaw.core.messagebuilder.MessageBuilder;
import io.sommers.aiintheipaw.core.user.IUser;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface IReceivedMessage extends IMessage {
    ProviderId getId();

    IChannel getChannel();

    Mono<String> replyTo(Function<MessageBuilder, MessageBuilder> messageBuilderFunction);

    IUser getUser();
}
