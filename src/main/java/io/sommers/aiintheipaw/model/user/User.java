package io.sommers.aiintheipaw.model.user;

public record User(
        long id
) implements IUser {
    @Override
    public long getId() {
        return this.id();
    }
}
