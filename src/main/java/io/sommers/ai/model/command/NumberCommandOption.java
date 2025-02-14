package io.sommers.ai.model.command;

import io.vavr.collection.Map;
import io.vavr.control.Validation;
import org.apache.commons.lang.math.Range;
import reactor.core.publisher.Mono;

import java.text.NumberFormat;
import java.text.ParseException;

public record NumberCommandOption(
        String name,
        String description,
        boolean required,
        Range range
) implements ICommandOption<Number> {
    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.description();
    }

    @Override
    public boolean isRequired() {
        return this.required();
    }

    @Override
    public Validation<String, Number> parseOption(String input) {
        try {
            Number number = NumberFormat.getInstance()
                    .parse(input);
            if (this.range().containsNumber(number)) {
                return Validation.valid(
                        NumberFormat.getInstance()
                                .parse(input)
                );
            } else {
                return Validation.invalid("%s is not between %d and %d".formatted(number, this.range().getMinimumLong(), this.range().getMaximumLong()));
            }
        } catch (ParseException parseException) {
            return Validation.invalid(parseException.getMessage());
        }
    }

    public Mono<Long> getOptionalLong(Map<String, Object> args) {
        return args.get(this.getName())
                .fold(
                        Mono::empty,
                        object -> {
                            if (object instanceof Number number) {
                                return Mono.just(number.longValue());
                            } else {
                                return Mono.empty();
                            }
                        }
                );
    }

    public Mono<Long> getLong(Map<String, Object> args) {
        return args.get(this.getName())
                .fold(
                        () -> Mono.error(new IllegalStateException("No value present for " + this.getName())),
                        object -> {
                            if (object instanceof Number number) {
                                return Mono.just(number.longValue());
                            } else {
                                return Mono.error(new IllegalStateException(object + " is not valid for " + this.getName()));
                            }
                        }
                );
    }
}
