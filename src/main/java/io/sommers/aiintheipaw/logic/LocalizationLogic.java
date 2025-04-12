package io.sommers.aiintheipaw.logic;

import io.quarkus.cache.CacheResult;
import io.sommers.aiintheipaw.util.ILocalizationProvider;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@ApplicationScoped
public class LocalizationLogic implements ILocalizationProvider {
    private final Map<Locale, Map<String, ResourceBundle>> resourceBundles;

    public LocalizationLogic() {
        this.resourceBundles = new HashMap<>();
    }

    @Override
    public String getLocalization(String translationKey, Object... args) {
        return this.getLocalization(Locale.US, "messages", translationKey);
    }

    @CacheResult(cacheName = "localization")
    public String getLocalization(Locale locale, String bundleName, String translationKey) {
        Map<String, ResourceBundle> bundles = resourceBundles.computeIfAbsent(locale, key -> new HashMap<>());
        ResourceBundle bundle = bundles.computeIfAbsent(bundleName, key -> loadBundle(locale, key));
        return bundle.getString(translationKey);
    }

    private ResourceBundle loadBundle(Locale locale, String bundle) {
        return ResourceBundle.getBundle("messages/" + bundle, locale);
    }
}
