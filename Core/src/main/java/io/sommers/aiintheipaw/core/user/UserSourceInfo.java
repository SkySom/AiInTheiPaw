package io.sommers.aiintheipaw.core.user;


import io.sommers.aiintheipaw.core.util.ProviderId;

public record UserSourceInfo(
        ProviderId id
) implements IUserSourceInfo {
    @Override
    public ProviderId getProviderId() {
        return this.id();
    }

    @Override
    public String getName() {
        return "Nobody";
    }
}
