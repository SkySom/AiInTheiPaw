package io.sommers.aiintheipaw.core.user;

import io.sommers.aiintheipaw.core.provider.IProvider;
import reactor.core.publisher.Mono;

public interface IUserSourceProvider extends IProvider {
    Mono<IUserSourceInfo> getUserSource(String userId);
}
