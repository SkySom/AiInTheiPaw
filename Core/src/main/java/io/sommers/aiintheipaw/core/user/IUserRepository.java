package io.sommers.aiintheipaw.core.user;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface IUserRepository extends ReactiveCrudRepository<User, UUID> {
}
