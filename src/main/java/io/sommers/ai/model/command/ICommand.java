package io.sommers.ai.model.command;

import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.model.message.IMessage;
import io.sommers.ai.model.message.IReceivedMessage;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public interface ICommand {
    @NotNull
    String getName();

    @NotNull
    String getDescription();

    @NotNull
    Array<ICommandOption<?>> getOptions();

    @NotNull
    Mono<Void> run(IReceivedMessage message, Map<String, Object> args);
}
