package io.sommers.aiintheipaw.config;

import org.eclipse.microprofile.config.spi.Converter;

import java.util.regex.Pattern;

public class CommandPatternConverter implements Converter<Pattern> {
    @Override
    public Pattern convert(String value) throws IllegalArgumentException, NullPointerException {
        if (value == null || value.isEmpty()) {
            throw new NullPointerException("value is null or empty");
        } else if (value.length() != 1) {
            throw new NullPointerException("value must only be a single character");
        } else {
            return Pattern.compile("^" + value + "(?<command>\\w+)\\s*(?<commandInput>[\\s\\w+]+)*$", Pattern.CASE_INSENSITIVE);
        }
    }
}
