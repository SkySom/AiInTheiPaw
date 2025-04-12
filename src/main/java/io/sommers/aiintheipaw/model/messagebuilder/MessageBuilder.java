package io.sommers.aiintheipaw.model.messagebuilder;

import io.sommers.aiintheipaw.model.message.BotMessage;
import io.sommers.aiintheipaw.model.message.IMessage;
import io.sommers.aiintheipaw.model.service.IService;
import io.sommers.aiintheipaw.model.user.IUser;
import io.sommers.aiintheipaw.util.ILocalizationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MessageBuilder {
    private final IService service;
    private String message;
    private String messageKey;

    private final List<Object> args;

    private final List<MessageBuilder> appendedMessages;

    public MessageBuilder(IService service) {
        this.service = service;
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

    public MessageBuilder withCommandArg(String commandName) {
        return withArg(commandName);
    }

    public MessageBuilder withUserArg(IUser user) {
        return withArg(user);
    }

    public MessageBuilder withMessageArg(MessageBuilderFunction subMessage) {
        return this.addArg(subMessage.apply(new MessageBuilder(this.service)));
    }

    public MessageBuilder withAppendedMessage(MessageBuilderFunction appendedMessage) {
        this.appendedMessages.add(appendedMessage.apply(new MessageBuilder(this.service)));
        return this;
    }

    private String buildString(Supplier<MessageBuilder> messageBuilderSupplier, ILocalizationProvider messageSource) {
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
            builtMessage = messageSource.getLocalization(messageKey, argArray);
        } else if (message != null) {
            builtMessage = message.formatted(argArray);
        }

        if (builtMessage == null) {
            return "Error: MissingNo.";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(builtMessage);

            for (MessageBuilder appendedMessage : appendedMessages) {
                stringBuilder.append(appendedMessage.build(messageBuilderSupplier, messageSource));
            }

            return stringBuilder.toString();
        }
    }

    public IMessage build(Supplier<MessageBuilder> messageBuilderSupplier, ILocalizationProvider messageSource) {
        return new BotMessage(this.buildString(messageBuilderSupplier, messageSource));
    }
}
