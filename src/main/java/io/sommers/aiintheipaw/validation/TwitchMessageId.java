package io.sommers.aiintheipaw.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TwitchMessageIdValidator.class)
public @interface TwitchMessageId {
    String message() default "{io.sommers.aiintheipaw.validation.TwitchMessageId.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
