package io.sommers.aiintheipaw.commander.command;

import io.sommers.aiintheipaw.core.message.IReceivedMessage;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import jakarta.validation.constraints.NotNull;
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
