package io.sommers.aiintheipaw.core.messagebuilder;

import java.util.Locale;

@FunctionalInterface
public interface MessageTranslationProvider {
    String getTranslation(String key, Object[] args, Locale locale);

    default String getTranslation(String key, Object[] args) {
        return getTranslation(key, args, Locale.ENGLISH);
    }
}
