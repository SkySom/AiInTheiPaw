package io.sommers.aiintheipaw.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxDurationValidator.class)
public @interface MaxDuration {
    long maxMillis();

    String message() default "{io.sommers.aiintheipaw.validation.MaxDuration.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
