package io.sommers.aiintheipaw.core.user;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface IUserRepository extends R2dbcRepository<User, UUID> {
}
