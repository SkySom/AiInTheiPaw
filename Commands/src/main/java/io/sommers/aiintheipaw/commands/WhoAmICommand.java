package io.sommers.aiintheipaw.commands;

import io.sommers.aiintheipaw.core.commander.ICommand;
import io.sommers.aiintheipaw.core.commander.ICommandOption;
import io.sommers.aiintheipaw.core.message.IReceivedMessage;
import io.sommers.aiintheipaw.core.user.UserSourceInfoService;
import io.sommers.aiintheipaw.core.user.source.UserSourceService;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class WhoAmICommand implements ICommand {
    private final UserSourceService userSourceService;

    public WhoAmICommand(UserSourceService userSourceService) {
        this.userSourceService = userSourceService;
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
        //TODO Fix whoami
        return message.replyTo(messageBuilder -> messageBuilder.withMessage("HI"))
                .then();
    }
}
