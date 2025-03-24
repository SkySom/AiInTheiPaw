package io.sommers.aiintheipaw.commander;

import io.smallrye.mutiny.Uni;
import io.vavr.control.Validation;

public interface ICommandOption<T> {
    String getName();

    String getDescription();

    boolean isRequired();

    Validation<String, T> parseOption(String input);
}
