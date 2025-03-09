package io.sommers.aiintheipaw.core.user.source;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface IUserSourceRepository extends ReactiveCrudRepository<UserSource, UUID> {
    Mono<UserSource> findUserSourceByServiceAndServiceId(String service, String serviceId);
}
