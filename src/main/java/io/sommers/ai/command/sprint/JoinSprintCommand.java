package io.sommers.ai.command.sprint;

import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.model.command.ICommand;
import io.sommers.ai.model.command.ICommandOption;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JoinSprintCommand implements ICommand {
    @Override
    public String getName() {
        return "wordsSprint";
    }

    @Override
    public String getDescription() {
        return "Join an active Sprint with previous word count";
    }

    @Override
    public ICommandOption[] getOptions() {
        return new ICommandOption[0];
    }

    @Override
    public Mono<Void> run(IChannel channel) {
        return null;
    }
}
