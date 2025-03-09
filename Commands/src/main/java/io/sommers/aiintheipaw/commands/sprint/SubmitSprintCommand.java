package io.sommers.aiintheipaw.commands.sprint;


import io.sommers.aiintheipaw.core.commander.ICommand;
import io.sommers.aiintheipaw.core.commander.ICommandOption;
import io.sommers.aiintheipaw.core.commander.NumberCommandOption;
import io.sommers.aiintheipaw.core.message.IReceivedMessage;
import io.sommers.aiintheipaw.core.util.IntRange;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import jakarta.validation.constraints.NotNull;
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
