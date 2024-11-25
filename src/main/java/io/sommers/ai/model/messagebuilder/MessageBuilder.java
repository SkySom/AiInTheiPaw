package io.sommers.ai.model.messagebuilder;

import io.sommers.ai.model.IMessage;
import io.sommers.ai.model.IUser;
import io.sommers.ai.model.Message;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public abstract MessageBuilder withMessageArg(MessageBuilder subMessage);

    public IMessage build(MessageSource messageSource) {
        Object[] argArray = new Object[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            Object arg = this.args.get(i);
            if (arg instanceof MessageBuilder messageBuilder) {
                argArray[i] = messageBuilder.build(messageSource);
            } else {
                argArray[i] = arg;
            }
        }

        if (messageKey != null) {
            return new Message(messageSource.getMessage(messageKey, argArray, Locale.US));
        } else if (message != null) {
            return new Message(message.formatted(argArray));
        } else {
            return new Message("Error: MissingNo.");
        }
    }
}
