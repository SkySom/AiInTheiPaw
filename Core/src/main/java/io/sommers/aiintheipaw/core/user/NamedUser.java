package io.sommers.aiintheipaw.core.user;


import io.sommers.aiintheipaw.core.util.ProviderId;

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
