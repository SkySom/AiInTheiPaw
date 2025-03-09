package io.sommers.aiintheipaw.twitch;

import io.sommers.aiintheipaw.core.commander.ICommandOption;
import io.sommers.aiintheipaw.core.commander.NumberCommandOption;
import io.sommers.aiintheipaw.core.util.IntRange;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.control.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBootTest
@SpringJUnitConfig
@TestConfiguration
public class TwitchCommandHandlerTests {

    @Autowired
    private TwitchCommandHandler twitchCommandHandler;

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
                twitchCommandHandler.parseOptions(Array.empty(), null)
        );
    }

    @Test
    void testRequiredCommandOptionWithNoInput() {
        Assertions.assertEquals(
                Validation.invalid("Required values missing"),
                twitchCommandHandler.parseOptions(SINGLE_OPTION_REQUIRED, null)
        );
    }

    @Test
    void testOptionalCommandOptionWithNoInput() {
        Assertions.assertEquals(
                Validation.valid(HashMap.empty()),
                twitchCommandHandler.parseOptions(SINGLE_OPTION, null)
        );
    }

    @Test
    void testValidCommandOptionWithInput() {
        Assertions.assertEquals(
                Validation.valid(HashMap.of("SINGLE_OPTION_REQUIRED", 1L)),
                twitchCommandHandler.parseOptions(SINGLE_OPTION_REQUIRED, "1")
        );
    }

    @Test
    void testValidTailingOptionalCommandOptionWithInput() {
        Assertions.assertEquals(
                Validation.valid(HashMap.of("SINGLE_OPTION_REQUIRED", 1L)),
                twitchCommandHandler.parseOptions(SINGLE_OPTION_REQUIRED.appendAll(SINGLE_OPTION), "1")
        );
    }

    @Test
    void testValidLeadingOptionalCommandOptionWithMissingInput() {
        Assertions.assertEquals(
                Validation.invalid("Required values missing"),
                twitchCommandHandler.parseOptions(SINGLE_OPTION.appendAll(SINGLE_OPTION_REQUIRED), "1")
        );
    }

    @Test
    void testValidLeadingOptionalCommandOptionWithInput() {
        Assertions.assertEquals(
                Validation.valid(HashMap.of("SINGLE_OPTION", 1L, "SINGLE_OPTION_REQUIRED", 1L)),
                twitchCommandHandler.parseOptions(SINGLE_OPTION.appendAll(SINGLE_OPTION_REQUIRED), "1 1")
        );
    }

    @Test
    void testInvalidOptionalCommandOptionWithInput() {
        Assertions.assertEquals(
                Validation.invalid("101 is not between 0 and 100"),
                twitchCommandHandler.parseOptions(SINGLE_OPTION, "101")
        );
    }

    @Test
    void testInvalidNumberCommandOptionWithString() {
        Assertions.assertEquals(
                Validation.invalid("Unparseable number: \"hello\""),
                twitchCommandHandler.parseOptions(SINGLE_OPTION, "hello")
        );
    }
}
