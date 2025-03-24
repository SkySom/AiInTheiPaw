package io.sommers.aiintheipaw.commander;

import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.model.message.IReceivedMessage;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import jakarta.validation.constraints.NotNull;

public interface ICommand {
    @NotNull
    String getName();

    @NotNull
    String getDescription();

    @NotNull
    Array<ICommandOption<?>> getOptions();

    @NotNull
    Uni<Void> run(IReceivedMessage message, Map<String, Object> args);
}
