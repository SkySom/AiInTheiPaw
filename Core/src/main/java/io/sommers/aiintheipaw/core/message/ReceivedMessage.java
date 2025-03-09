package io.sommers.aiintheipaw.core.message;

import io.sommers.aiintheipaw.core.channel.IChannel;
import io.sommers.aiintheipaw.core.messagebuilder.MessageBuilder;
import io.sommers.aiintheipaw.core.user.User;
import io.sommers.aiintheipaw.core.util.ProviderId;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public record ReceivedMessage(
        ProviderId id,
        String text,
        IChannel channel,
        User user
) implements IReceivedMessage {
    @Override
    public String getText() {
        return this.text();
    }

    @Override
    public ProviderId getId() {
        return this.id();
    }

    @Override
    public IChannel getChannel() {
        return this.channel();
    }

    public Mono<String> replyTo(Function<MessageBuilder, MessageBuilder> messageBuilderFunction) {
        return this.getChannel()
                .sendMessage(
                        this.id()
                                .id(),
                        messageBuilderFunction
                );
    }

    @Override
    public User getUser() {
        return this.user();
    }
}
