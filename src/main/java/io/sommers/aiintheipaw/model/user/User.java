package io.sommers.aiintheipaw.model.user;

public record User(
        UserEntity userEntity
) implements IUser {
    @Override
    public long getId() {
        return this.userEntity().getId();
    }
}
