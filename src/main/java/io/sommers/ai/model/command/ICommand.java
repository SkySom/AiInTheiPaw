package io.sommers.ai.model.command;

import io.sommers.ai.model.channel.IChannel;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import reactor.core.publisher.Mono;

public interface ICommand {
    String getName();

    String getDescription();

    Array<ICommandOption<?>> getOptions();

    Mono<Void> run(IChannel channel, Map<String, Object> args);
}
