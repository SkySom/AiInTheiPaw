package io.sommers.aiintheipaw.core.util;

public record ProviderId(
        String provider,
        String id
) {
    public ProviderId(String providerId) {
        this(providerId.substring(0, providerId.indexOf("_")), providerId.substring(providerId.indexOf("_") + 1));
    }

    public String asDocumentKey() {
        return provider + "_" + id;
    }
}
