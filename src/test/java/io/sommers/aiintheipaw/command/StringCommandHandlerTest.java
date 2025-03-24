package io.sommers.aiintheipaw.command;

import io.quarkus.test.junit.QuarkusTest;
import io.sommers.aiintheipaw.commander.ICommandOption;
import io.sommers.aiintheipaw.commander.NumberCommandOption;
import io.sommers.aiintheipaw.commander.StringCommandHandler;
import io.sommers.aiintheipaw.util.IntRange;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.control.Validation;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StringCommandHandlerTest {

    @Inject
    StringCommandHandler stringCommandHandler;

    private static final Array<ICommandOption<?>> SINGLE_OPTION_REQUIRED = Array.of(
            new NumberCommandOption("SINGLE_OPTION_REQUIRED", "", true, new IntRange(0, 100))
    );

    private static final Array<ICommandOption<?>> SINGLE_OPTION = Array.of(
            new NumberCommandOption("SINGLE_OPTION", "", false, new IntRange(0, 100))
    );

    @Test
    void testNoCommandOptionsWithNoInput() {
        Assertions.assertEquals(
                Validation.valid(HashMap.empty()),
                stringCommandHandler.parseOptions(Array.empty(), null)
        );
    }

    @Test
    void testRequiredCommandOptionWithNoInput() {
        Assertions.assertEquals(
                Validation.invalid("Required values missing"),
                stringCommandHandler.parseOptions(SINGLE_OPTION_REQUIRED, null)
        );
    }

    @Test
    void testOptionalCommandOptionWithNoInput() {
        Assertions.assertEquals(
                Validation.valid(HashMap.empty()),
                stringCommandHandler.parseOptions(SINGLE_OPTION, null)
        );
    }

    @Test
    void testValidCommandOptionWithInput() {
        Assertions.assertEquals(
                Validation.valid(HashMap.of("SINGLE_OPTION_REQUIRED", 1L)),
                stringCommandHandler.parseOptions(SINGLE_OPTION_REQUIRED, "1")
        );
    }

    @Test
    void testValidTailingOptionalCommandOptionWithInput() {
        Assertions.assertEquals(
                Validation.valid(HashMap.of("SINGLE_OPTION_REQUIRED", 1L)),
                stringCommandHandler.parseOptions(SINGLE_OPTION_REQUIRED.appendAll(SINGLE_OPTION), "1")
        );
    }

    @Test
    void testValidLeadingOptionalCommandOptionWithMissingInput() {
        Assertions.assertEquals(
                Validation.invalid("Required values missing"),
                stringCommandHandler.parseOptions(SINGLE_OPTION.appendAll(SINGLE_OPTION_REQUIRED), "1")
        );
    }

    @Test
    void testValidLeadingOptionalCommandOptionWithInput() {
        Assertions.assertEquals(
                Validation.valid(HashMap.of("SINGLE_OPTION", 1L, "SINGLE_OPTION_REQUIRED", 1L)),
                stringCommandHandler.parseOptions(SINGLE_OPTION.appendAll(SINGLE_OPTION_REQUIRED), "1 1")
        );
    }

    @Test
    void testInvalidOptionalCommandOptionWithInput() {
        Assertions.assertEquals(
                Validation.invalid("101 is not between 0 and 100"),
                stringCommandHandler.parseOptions(SINGLE_OPTION, "101")
        );
    }

    @Test
    void testInvalidNumberCommandOptionWithString() {
        Assertions.assertEquals(
                Validation.invalid("Unparseable number: \"hello\""),
                stringCommandHandler.parseOptions(SINGLE_OPTION, "hello")
        );
    }
}
