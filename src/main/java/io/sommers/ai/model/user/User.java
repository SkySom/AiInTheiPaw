package io.sommers.ai.model.user;

import io.sommers.ai.model.ProviderId;

public record User(
        ProviderId id
) implements IUser {
    @Override
    public ProviderId getProviderId() {
        return this.id();
    }

    @Override
    public String getName() {
        return "Nobody";
    }
}
