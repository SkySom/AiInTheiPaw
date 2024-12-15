package io.sommers.ai.model.messagebuilder;

import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.message.BotMessage;
import io.sommers.ai.model.message.IMessage;
import io.sommers.ai.model.user.IUser;
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

    private final List<MessageBuilderFunction> appendedMessages;

    public MessageBuilder() {
        this.args = new ArrayList<>();
        this.appendedMessages = new ArrayList<>();
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

    public abstract MessageBuilder withUserArg(ProviderId user);

    public MessageBuilder withMessageArg(MessageBuilderFunction subMessage) {
        return this.addArg(subMessage);
    }

    public MessageBuilder withAppendedMessage(MessageBuilderFunction appendedMessage) {
        this.appendedMessages.add(appendedMessage);
        return this;
    }

    private String buildString(Supplier<MessageBuilder> messageBuilderSupplier, MessageSource messageSource) {
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

        String builtMessage = null;

        if (messageKey != null) {
            builtMessage = messageSource.getMessage(messageKey, argArray, Locale.US);
        } else if (message != null) {
            builtMessage = message.formatted(argArray);
        }

        if (builtMessage == null) {
            return "Error: MissingNo.";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(builtMessage);

            for (MessageBuilderFunction appendedMessage : appendedMessages) {
                stringBuilder.append(appendedMessage.apply(messageBuilderSupplier.get())
                        .build(messageBuilderSupplier, messageSource)
                );
            }

            return stringBuilder.toString();
        }
    }

    public IMessage build(Supplier<MessageBuilder> messageBuilderSupplier, MessageSource messageSource) {
        return new BotMessage(this.buildString(messageBuilderSupplier, messageSource));
    }
}
