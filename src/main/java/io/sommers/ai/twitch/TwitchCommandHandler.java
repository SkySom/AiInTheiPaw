package io.sommers.ai.twitch;

import com.github.twitch4j.eventsub.EventSubNotification;
import com.github.twitch4j.eventsub.domain.chat.Message;
import com.github.twitch4j.eventsub.events.ChannelChatMessageEvent;
import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.command.ICommand;
import io.sommers.ai.model.command.ICommandOption;
import io.sommers.ai.model.message.ReceivedMessage;
import io.sommers.ai.model.user.User;
import io.sommers.ai.twitch.model.TwitchChannel;
import io.sommers.ai.twitch.model.TwitchMessageService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Validation;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitchCommandHandler {
    private final Map<String, ICommand> commands;
    private final Pattern commandPattern;
    private final TwitchMessageService twitchMessageService;

    public TwitchCommandHandler(TwitchConfiguration twitchConfiguration, List<ICommand> commands, TwitchMessageService twitchMessageService) {
        this.commands = commands.stream()
                .collect(HashMap.collector(ICommand::getName));
        this.commandPattern = Pattern.compile("^" + twitchConfiguration.getCommandPrefix() + "(?<command>\\w+)\\s*(?<commandInput>[\\s\\w+]+)*$", Pattern.CASE_INSENSITIVE);
        this.twitchMessageService = twitchMessageService;
    }

    public Mono<Void> tryExecuteCommand(@NotNull EventSubNotification notification) {
        if (notification.getEvent() instanceof ChannelChatMessageEvent channelChatMessageEvent) {
            Message message = channelChatMessageEvent.getMessage();
            if (!message.isAction()) {
                return tryExecuteCommand(message.getText(), channelChatMessageEvent.getBroadcasterUserId(),
                        channelChatMessageEvent.getMessageId(), channelChatMessageEvent.getChatterUserId());
            }
        }

        return Mono.empty();
    }

    public Mono<Void> tryExecuteCommand(String text, String broadcasterId, String messageId, String chatterId) {
        Matcher matcher = this.commandPattern.matcher(text);

        if (matcher.find()) {
            String commandName = matcher.group("command");
            String input = matcher.group("commandInput");
            return this.commands.get(commandName)
                    .map(command -> parseOptions(command.getOptions(), input)
                            .fold(
                                    error -> new TwitchChannel(this.twitchMessageService, broadcasterId)
                                            .sendMessage(
                                                    messageId,
                                                    messageBuilder -> messageBuilder.withKey("twitch.error")
                                                            .withArg(error)
                                            )
                                            .then(),
                                    args -> command.run(
                                            new ReceivedMessage(
                                                    new ProviderId(TwitchConstants.PROVIDER, messageId),
                                                    text,
                                                    new TwitchChannel(this.twitchMessageService, broadcasterId),
                                                    new User(new ProviderId(TwitchConstants.PROVIDER, chatterId))
                                            ),
                                            args
                                    )
                            )
                    )
                    .getOrElse(Mono.empty());
        }

        return Mono.empty();
    }

    public Validation<String, Map<String, Object>> parseOptions(Array<ICommandOption<?>> options, String input) {
        if (input != null && input.isBlank()) {
            input = null;
        }

        if (options.isEmpty() && input == null) {
            return Validation.valid(HashMap.empty());
        } else if (options.isEmpty()) {
            return Validation.invalid("No options found, but input included");
        } else if (input != null) {
            Iterator<ICommandOption<?>> commandOptionIterator = options.iterator();
            Array<String> commandInputs = Array.of(input.trim()
                    .split("\\s+")
            );
            Map<String, Object> args = HashMap.empty();
            return parseOption(commandInputs, commandOptionIterator, args);
        } else {
            boolean hasRequired = options.exists(ICommandOption::isRequired);
            if (hasRequired) {
                return Validation.invalid("Required values missing");
            } else {
                return Validation.valid(HashMap.empty());
            }
        }
    }

    private Validation<String, Map<String, Object>> parseOption(Array<String> commandInput, Iterator<ICommandOption<?>> options, Map<String, Object> args) {
        Option<String> nextInput = commandInput.headOption();
        return nextInput.fold(
                () -> onNoInput(options, args),
                input -> parseOption(input, options)
                        .flatMap(tuple -> parseOption(commandInput.tail(), options, args.put(tuple)))
        );
    }

    private Validation<String, Tuple2<String, Object>> parseOption(String commandInput, Iterator<ICommandOption<?>> options) {
        Validation<String, Tuple2<String, Object>> parsedOption = null;
        while (options.hasNext() && parsedOption == null) {
            ICommandOption<?> commandOption = options.next();
            Validation<String, ?> currentParse = commandOption.parseOption(commandInput);
            if (currentParse.isInvalid() && (commandOption.isRequired() || !options.hasNext())) {
                parsedOption = currentParse.map(option -> null);
            } else if (currentParse.isValid()) {
                parsedOption = currentParse.map(value -> Tuple.of(commandOption.getName(), value));
            }
        }
        return parsedOption != null ? parsedOption : Validation.invalid("Found input with no matching option");
    }

    private Validation<String, Map<String, Object>> onNoInput(Iterator<ICommandOption<?>> options, Map<String, Object> args) {
        if (options.exists(ICommandOption::isRequired)) {
            return Validation.invalid("Required values missing");
        } else {
            return Validation.valid(args);
        }
    }
}
