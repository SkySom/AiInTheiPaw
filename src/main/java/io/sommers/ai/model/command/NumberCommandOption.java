package io.sommers.ai.model.command;

import io.vavr.control.Validation;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;

import java.text.NumberFormat;
import java.text.ParseException;

public record NumberCommandOption(
        String name,
        String description,
        boolean required,
        Range range
) implements ICommandOption<Number> {
    public NumberCommandOption(String name, String description) {
        this(name, description, false, new IntRange(Integer.MIN_VALUE, Integer.MAX_VALUE));
    }

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
}
