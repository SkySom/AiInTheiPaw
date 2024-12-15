package io.sommers.ai.command.sprint;

import io.sommers.ai.model.command.ICommand;
import io.sommers.ai.model.command.ICommandOption;
import io.sommers.ai.model.message.IReceivedMessage;
import io.sommers.ai.service.SprintService;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class StartSprintCommand implements ICommand {

    private final SprintService sprintService;

    public StartSprintCommand(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @Override
    @NotNull
    public String getName() {
        return "startSprint";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "Starts a Writing Sprint";
    }

    @Override
    @NotNull
    public Array<ICommandOption<?>> getOptions() {
        return Array.empty();
    }

    @Override
    @NotNull
    public Mono<Void> run(IReceivedMessage message, Map<String, Object> args) {
        return this.sprintService.setSprintToSignUp(message);
    }
}
