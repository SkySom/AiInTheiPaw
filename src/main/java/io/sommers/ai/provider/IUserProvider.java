package io.sommers.ai.provider;

import io.sommers.ai.model.user.IUser;
import reactor.core.publisher.Mono;

public interface IUserProvider extends IProvider {
    Mono<IUser> getUser(String userId);
}
