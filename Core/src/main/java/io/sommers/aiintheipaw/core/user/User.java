package io.sommers.aiintheipaw.core.user;


import io.sommers.aiintheipaw.core.util.ProviderId;

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
