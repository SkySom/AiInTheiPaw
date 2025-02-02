package io.sommers.aiintheipaw.commander.command;

import io.vavr.control.Validation;

public interface ICommandOption<T> {
    String getName();

    String getDescription();

    boolean isRequired();

    Validation<String, T> parseOption(String input);
}
