package io.sommers.ai.command.sprint;

import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.model.command.ICommand;
import io.sommers.ai.model.command.ICommandOption;
import io.sommers.ai.service.SprintService;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class StartSprintCommand implements ICommand {

    private final SprintService sprintService;

    public StartSprintCommand(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @Override
    public String getName() {
        return "startSprint";
    }

    @Override
    public String getDescription() {
        return "Starts a Writing Sprint";
    }

    @Override
    public Array<ICommandOption<?>> getOptions() {
        return Array.empty();
    }

    @Override
    public Mono<Void> run(IChannel channel, Map<String, Object> args) {
        return this.sprintService.setSprintToSignUp(channel);
    }
}
