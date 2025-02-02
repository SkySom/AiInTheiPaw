package io.sommers.aiintheipaw.core.user;

import io.sommers.aiintheipaw.core.provider.IProvider;
import reactor.core.publisher.Mono;

public interface IUserProvider extends IProvider {
    Mono<IUser> getUser(String userId);
}
