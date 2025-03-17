package io.sommers.aiintheipaw.validation;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.concurrent.TimeUnit;

public class TwitchMessageIdValidator implements ConstraintValidator<TwitchMessageId, String> {
    private final Cache<String, Boolean> RECENT_MESSAGE_IDS = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Override
    public boolean isValid(String messageId, ConstraintValidatorContext constraintValidatorContext) {
        boolean verified = messageId != null && !messageId.isEmpty() && RECENT_MESSAGE_IDS.getIfPresent(messageId) == null;
        if (verified) {
            RECENT_MESSAGE_IDS.put(messageId, true);
        }
        return verified;
    }
}
