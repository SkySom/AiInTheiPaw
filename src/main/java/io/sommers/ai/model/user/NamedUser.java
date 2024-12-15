package io.sommers.ai.model.user;

import io.sommers.ai.model.ProviderId;

public record NamedUser(
        ProviderId id,
        String displayName
) implements IUser {
    @Override
    public ProviderId getProviderId() {
        return this.id();
    }

    @Override
    public String getName() {
        return this.displayName();
    }
}
