package io.sommers.aiintheipaw.commander;

import io.smallrye.config.WithConverter;
import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.config.CommandPatternConverter;
import io.sommers.aiintheipaw.model.message.IReceivedMessage;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Validation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class StringCommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringCommandHandler.class);

    @Inject
    Instance<ICommand> commands;

    @WithConverter(CommandPatternConverter.class)
    @ConfigProperty(name = "command.prefix")
    Pattern commandPattern;

    public Uni<Void> tryExecuteCommand(IReceivedMessage receivedMessage) {
        Matcher matcher = this.commandPattern.matcher(receivedMessage.getText());

        if (matcher.find()) {
            String commandName = matcher.group("command");
            String input = matcher.group("commandInput");
            return Uni.createFrom()
                    .optional(this.commands.stream()
                            .filter(command -> command.getName()
                                    .equalsIgnoreCase(commandName)
                            )
                            .findFirst()
                    )
                    .flatMap(command -> parseOptions(command.getOptions(), input)
                            .fold(
                                    error -> Uni.createFrom()
                                            .voidItem(),
                                    args -> command.run(
                                            receivedMessage,
                                            args
                                    ))
                    );
        }

        return Uni.createFrom()
                .voidItem();
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
