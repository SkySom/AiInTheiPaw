package io.sommers.aiintheipaw.core.user.source;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface IUserSourceRepository extends R2dbcRepository<UserSource, UUID> {
    Mono<UserSource> findUserSourceByServiceAndServiceId(String service, String serviceId);
}
