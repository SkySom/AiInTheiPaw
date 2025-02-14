package io.sommers.ai.command.sprint;

import io.sommers.ai.model.command.ICommand;
import io.sommers.ai.model.command.ICommandOption;
import io.sommers.ai.model.command.NumberCommandOption;
import io.sommers.ai.model.message.IReceivedMessage;
import io.sommers.ai.service.SprintService;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import org.apache.commons.lang.math.IntRange;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JoinSprintCommand implements ICommand {
    private static final NumberCommandOption WORD_COUNT = new NumberCommandOption(
            "Word Count",
            "The number of words to start the sprint with.",
            false,
            new IntRange(0, Integer.MAX_VALUE)
    );

    private static final Array<ICommandOption<?>> COMMAND_OPTIONS = Array.of(
            WORD_COUNT
    );

    private final SprintService sprintService;

    public JoinSprintCommand(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @Override
    @NotNull
    public String getName() {
        return "joinSprint";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "Join an active Sprint with previous word count";
    }

    @Override
    @NotNull
    public Array<ICommandOption<?>> getOptions() {
        return COMMAND_OPTIONS;
    }

    @Override
    @NotNull
    public Mono<Void> run(IReceivedMessage message, Map<String, Object> args) {
        return this.sprintService.joinSprint(message, WORD_COUNT.getOptionalLong(args))
                .then();
    }
}
