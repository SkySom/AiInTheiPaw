package io.sommers.aiintheipaw.core.user;


import io.sommers.aiintheipaw.core.util.ProviderId;

public record NamedUserSourceInfo(
        ProviderId id,
        String displayName
) implements IUserSourceInfo {
    @Override
    public ProviderId getProviderId() {
        return this.id();
    }

    @Override
    public String getName() {
        return this.displayName();
    }
}
