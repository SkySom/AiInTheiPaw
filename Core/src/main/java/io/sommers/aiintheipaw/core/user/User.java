package io.sommers.aiintheipaw.core.user;

import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.user.IUser;

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
