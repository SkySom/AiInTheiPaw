package io.sommers.ai.twitch;

import com.github.twitch4j.eventsub.EventSubNotification;
import com.github.twitch4j.eventsub.domain.chat.Message;
import com.github.twitch4j.eventsub.events.ChannelChatMessageEvent;
import io.sommers.ai.model.command.ICommand;
import io.sommers.ai.twitch.model.TwitchChannel;
import io.sommers.ai.twitch.model.TwitchMessageService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TwitchCommandHandler {
    private final Map<String, ICommand> commands;
    private final Pattern commandPattern;
    private final TwitchMessageService twitchMessageService;

    public TwitchCommandHandler(TwitchConfiguration twitchConfiguration, List<ICommand> commands, TwitchMessageService twitchMessageService) {
        this.commands = commands.stream()
                .collect(Collectors.toMap(ICommand::getName, Function.identity()));
        this.commandPattern = Pattern.compile("^" + twitchConfiguration.getCommandPrefix() + "(?<command>\\w+)\\s*(?<commandInput>[\\s\\w+]+)*$", Pattern.CASE_INSENSITIVE);
        this.twitchMessageService = twitchMessageService;
    }

    public Mono<Void> tryExecuteCommand(@NotNull EventSubNotification notification) {
        if (notification.getEvent() instanceof ChannelChatMessageEvent channelChatMessageEvent) {
            Message message = channelChatMessageEvent.getMessage();
            if (!message.isAction()) {
                return tryExecuteCommand(message.getText(), channelChatMessageEvent.getBroadcasterUserId());
            }
        }

        return Mono.empty();
    }

    public Mono<Void> tryExecuteCommand(String text, String broadcasterId) {
        Matcher matcher = this.commandPattern.matcher(text);

        if (matcher.find()) {
            String commandName = matcher.group("command");
            String input = matcher.group("commandInput");
            ICommand command = this.commands.get(commandName);

            if (command != null) {
                return command.run(new TwitchChannel(this.twitchMessageService, broadcasterId));
            }
        }

        return Mono.empty();
    }

    @Nullable
    public ICommand findCommand(String command) {
        Matcher matcher = this.commandPattern.matcher(command);

        if (matcher.find()) {
            String commandName = matcher.group("command");
            String input = matcher.group("commandInput");
            return this.commands.get(commandName);
        } else {
            return null;
        }
    }
}
