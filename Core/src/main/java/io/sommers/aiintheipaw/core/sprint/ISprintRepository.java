package io.sommers.aiintheipaw.core.sprint;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ISprintRepository extends R2dbcRepository<Sprint, UUID> {
    Flux<Sprint> findByChannelId(String channelId, Pageable pageable);
}
