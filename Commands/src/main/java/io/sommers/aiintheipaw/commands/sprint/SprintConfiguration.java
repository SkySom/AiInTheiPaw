package io.sommers.aiintheipaw.commands.sprint;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "command.sprint")
public class SprintConfiguration {
    @NotNull
    private final Duration signUpDuration;
    @NotNull
    private final Duration inProgressDuration;
    @NotNull
    private final Duration awaitingCountsDuration;

    public SprintConfiguration(Duration signUpDuration, Duration inProgressDuration, Duration awaitingCountsDuration) {
        this.signUpDuration = signUpDuration;
        this.inProgressDuration = inProgressDuration;
        this.awaitingCountsDuration = awaitingCountsDuration;
    }

    public Duration getSignUpDuration() {
        return signUpDuration;
    }

    public Duration getInProgressDuration() {
        return inProgressDuration;
    }

    public Duration getAwaitingCountsDuration() {
        return awaitingCountsDuration;
    }
}
