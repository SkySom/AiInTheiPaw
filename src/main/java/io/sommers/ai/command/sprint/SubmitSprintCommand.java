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
public class SubmitSprintCommand implements ICommand {
    private static final NumberCommandOption WORD_COUNT = new NumberCommandOption(
            "Word Count",
            "The number of words to submit.",
            true,
            new IntRange(0, Integer.MAX_VALUE)
    );

    private static final Array<ICommandOption<?>> COMMAND_OPTIONS = Array.of(
            WORD_COUNT
    );

    private final SprintService sprintService;

    public SubmitSprintCommand(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @Override
    public @NotNull String getName() {
        return "submitSprint";
    }

    @Override
    public @NotNull String getDescription() {
        return "Submit the final word count for a sprint";
    }

    @Override
    public @NotNull Array<ICommandOption<?>> getOptions() {
        return COMMAND_OPTIONS;
    }

    @Override
    public @NotNull Mono<Void> run(IReceivedMessage message, Map<String, Object> args) {
        return WORD_COUNT.getLong(args)
                .flatMap(wordCount -> this.sprintService.submitWords(message, wordCount))
                .then();
    }
}
