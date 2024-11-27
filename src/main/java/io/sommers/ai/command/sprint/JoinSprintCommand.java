package io.sommers.ai.command.sprint;

import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.model.command.ICommand;
import io.sommers.ai.model.command.ICommandOption;
import io.sommers.ai.model.command.NumberCommandOption;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import org.apache.commons.lang.math.IntRange;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JoinSprintCommand implements ICommand {
    private static final String OPTION_NAME = "Word Count";

    private static final Array<ICommandOption<?>> COMMAND_OPTIONS = Array.of(
            new NumberCommandOption(OPTION_NAME, "The number of words to start the sprint with.", false, new IntRange(1, Integer.MAX_VALUE))
    );

    @Override
    public String getName() {
        return "wordsSprint";
    }

    @Override
    public String getDescription() {
        return "Join an active Sprint with previous word count";
    }

    @Override
    public Array<ICommandOption<?>> getOptions() {
        return COMMAND_OPTIONS;
    }

    @Override
    public Mono<Void> run(IChannel channel, Map<String, Object> args) {
        return null;
    }
}
