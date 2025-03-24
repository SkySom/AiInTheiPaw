package io.sommers.aiintheipaw.commander;

import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.util.IntRange;
import io.vavr.control.Validation;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.Map;

public record NumberCommandOption(
        String name,
        String description,
        boolean required,
        IntRange range
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
            if (this.range().isInRange(number)) {
                return Validation.valid(
                        NumberFormat.getInstance()
                                .parse(input)
                );
            } else {
                return Validation.invalid("%s is not between %d and %d".formatted(number, this.range().min(), this.range().max()));
            }
        } catch (ParseException parseException) {
            return Validation.invalid(parseException.getMessage());
        }
    }

    public Uni<Long> getOptionalLong(Map<String, Object> args) {
        return Uni.createFrom()
                .item(args.get(this.getName()))
                .flatMap(value -> {
                    if (value instanceof Number number) {
                        return Uni.createFrom()
                                .item(number.longValue());
                    } else {
                        return Uni.createFrom()
                                .nullItem();
                    }
                });
    }

    public Uni<Long> getLong(Map<String, Object> args) {
        return Uni.createFrom()
                .item(args.get(this.getName()))
                .flatMap(value -> {
                    if (value instanceof Number number) {
                        return Uni.createFrom()
                                .item(number.longValue());
                    } else {
                        return Uni.createFrom()
                                .failure(new IllegalStateException(value + " is not valid for " + this.getName()));
                    }
                })
                .ifNoItem()
                .after(Duration.ZERO)
                .failWith(() -> new IllegalStateException("No value present for " + this.getName()));
    }
}
