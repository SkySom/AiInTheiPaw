package io.sommers.ai.command;

import io.sommers.ai.model.command.ICommand;
import io.sommers.ai.model.command.ICommandOption;
import io.sommers.ai.model.message.IReceivedMessage;
import io.sommers.ai.service.UserService;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class WhoAmICommand implements ICommand {
    private final UserService userService;

    public WhoAmICommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public @NotNull String getName() {
        return "whoami";
    }

    @Override
    public @NotNull String getDescription() {
        return "Returns info on the current user";
    }

    @Override
    public @NotNull Array<ICommandOption<?>> getOptions() {
        return Array.empty();
    }

    @Override
    public @NotNull Mono<Void> run(IReceivedMessage message, Map<String, Object> args) {
        return this.userService.getUser(message.getUser().getProviderId())
                .flatMap(user -> message.replyTo(messageBuilder -> messageBuilder.withMessage(user.toString())))
                .then();
    }
}
