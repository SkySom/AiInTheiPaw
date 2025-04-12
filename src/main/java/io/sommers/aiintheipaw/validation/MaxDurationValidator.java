package io.sommers.aiintheipaw.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class MaxDurationValidator implements ConstraintValidator<MaxDuration, String> {
    private Duration maxDuration;

    @Override
    public boolean isValid(String timestamp, ConstraintValidatorContext constraintValidatorContext) {
        if (timestamp == null || timestamp.isEmpty()) {
            return false;
        } else {
            try {
                Instant now = constraintValidatorContext.getClockProvider()
                        .getClock()
                        .instant();
                Instant instant = Instant.parse(timestamp);
                return Duration.between(instant, now).compareTo(maxDuration) < 0;
            } catch (DateTimeParseException e) {
                return false;
            }
        }
    }

    @Override
    public void initialize(MaxDuration constraintAnnotation) {
        this.maxDuration = Duration.of(constraintAnnotation.maxMillis(), ChronoUnit.MILLIS);
    }
}
