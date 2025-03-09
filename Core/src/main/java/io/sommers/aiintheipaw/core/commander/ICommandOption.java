package io.sommers.aiintheipaw.core.commander;

import io.vavr.control.Validation;

public interface ICommandOption<T> {
    String getName();

    String getDescription();

    boolean isRequired();

    Validation<String, T> parseOption(String input);
}
