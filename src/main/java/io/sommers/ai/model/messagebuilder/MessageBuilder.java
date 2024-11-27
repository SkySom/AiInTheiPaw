package io.sommers.ai.model.messagebuilder;

import io.sommers.ai.model.message.BotMessage;
import io.sommers.ai.model.message.IMessage;
import io.sommers.ai.model.user.IUser;
import io.sommers.ai.model.message.ReceivedMessage;
import io.sommers.ai.util.MessageBuilderFunction;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public abstract class MessageBuilder {
    private String message;
    private String messageKey;

    private final List<Object> args;

    public MessageBuilder() {
        this.args = new ArrayList<>();
    }

    protected MessageBuilder addArg(Object arg) {
        this.args.add(arg);
        return this;
    }

    public MessageBuilder withKey(String messageKey) {
        this.messageKey = messageKey;
        return this;
    }

    public MessageBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public MessageBuilder withArg(Object arg) {
        this.args.add(arg);
        return this;
    }

    public abstract MessageBuilder withCommandArg(String commandName);

    public abstract MessageBuilder withUserArg(IUser user);

    public MessageBuilder withMessageArg(MessageBuilderFunction subMessage) {
        return this.addArg(subMessage);
    }

    public IMessage build(Supplier<MessageBuilder> messageBuilderSupplier, MessageSource messageSource) {
        Object[] argArray = new Object[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            Object arg = this.args.get(i);
            if (arg instanceof MessageBuilderFunction messageBuilder) {
                argArray[i] = messageBuilder.apply(messageBuilderSupplier.get())
                        .build(messageBuilderSupplier, messageSource);
            } else {
                argArray[i] = arg;
            }
        }

        if (messageKey != null) {
            return new BotMessage(messageSource.getMessage(messageKey, argArray, Locale.US));
        } else if (message != null) {
            return new BotMessage(message.formatted(argArray));
        } else {
            return new BotMessage("Error: MissingNo.");
        }
    }
}
