package io.sommers.ai.model.command;

import io.sommers.ai.model.channel.IChannel;
import reactor.core.publisher.Mono;

public interface ICommand {
    String getName();

    String getDescription();

    ICommandOption[] getOptions();

    Mono<Void> run(IChannel channel);
}
