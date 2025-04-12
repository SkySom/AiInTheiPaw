package io.sommers.aiintheipaw.util;

public interface ILocalizationProvider {
    String getLocalization(String translationKey, Object... args);
}
