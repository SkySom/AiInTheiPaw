package io.sommers.ai.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "commands.sprint")
public class SprintConfiguration {
    @NotNull
    private final Duration signUpDuration;
    @NotNull
    private final Duration inProgressionDuration;
    @NotNull
    private final Duration awaitingCountsDuration;

    public SprintConfiguration(Duration signUpDuration, Duration inProgressionDuration, Duration awaitingCountsDuration) {
        this.signUpDuration = signUpDuration;
        this.inProgressionDuration = inProgressionDuration;
        this.awaitingCountsDuration = awaitingCountsDuration;
    }

    public Duration getSignUpDuration() {
        return signUpDuration;
    }

    public Duration getInProgressionDuration() {
        return inProgressionDuration;
    }

    public Duration getAwaitingCountsDuration() {
        return awaitingCountsDuration;
    }
}
