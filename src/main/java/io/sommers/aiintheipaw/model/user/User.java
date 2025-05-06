package io.sommers.aiintheipaw.model.user;

import io.sommers.aiintheipaw.entity.UserEntity;

public record User(
        UserEntity userEntity
) implements IUser {
    @Override
    public long getId() {
        return this.userEntity().getId();
    }
}
