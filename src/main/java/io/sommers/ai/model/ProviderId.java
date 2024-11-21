package io.sommers.ai.model;

public record ProviderId(
        String provider,
        String id
) {

    public String asDocumentKey() {
        return provider + id;
    }
}
