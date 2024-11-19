package io.sommers.ai.twitch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.Assert;

@SpringBootTest
@SpringJUnitConfig
@TestConfiguration
public class TwitchCommandHandlerTests {

    @Autowired
    private TwitchCommandHandler twitchCommandHandler;

    @Test
    void testFindCommandWithNoInput() {
        Assert.notNull(twitchCommandHandler.findCommand("!sprintstart"), "Failed to find command");
    }

    @Test
    void testFindCommandWithInput() {
        Assert.notNull(twitchCommandHandler.findCommand("!sprintstart 20 minutes"), "Failed to find command");
    }
}
