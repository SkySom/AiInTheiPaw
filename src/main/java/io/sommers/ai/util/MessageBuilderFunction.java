package io.sommers.ai.util;

import io.sommers.ai.model.messagebuilder.MessageBuilder;

import java.util.function.Function;

public interface MessageBuilderFunction extends Function<MessageBuilder, MessageBuilder> {
}
