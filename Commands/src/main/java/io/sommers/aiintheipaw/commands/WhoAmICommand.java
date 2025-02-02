package io.sommers.aiintheipaw.commands;

import io.sommers.aiintheipaw.commander.command.ICommand;
import io.sommers.aiintheipaw.commander.command.ICommandOption;
import io.sommers.aiintheipaw.core.message.IReceivedMessage;
import io.sommers.aiintheipaw.core.user.UserService;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import jakarta.validation.constraints.NotNull;
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
