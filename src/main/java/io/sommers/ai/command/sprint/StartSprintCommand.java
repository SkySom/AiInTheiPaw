package io.sommers.ai.command.sprint;

import io.sommers.ai.model.IChannel;
import io.sommers.ai.model.Message;
import io.sommers.ai.model.command.ICommand;
import io.sommers.ai.model.command.ICommandOption;
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
    public ICommandOption[] getOptions() {
        return new ICommandOption[0];
    }

    @Override
    public Mono<Void> run(IChannel channel) {
        return this.sprintService.scheduleSprint(channel);
    }
}
