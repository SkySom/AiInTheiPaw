package io.sommers.ai.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import io.sommers.ai.model.sprint.Sprint;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ISprintRepository extends FirestoreReactiveRepository<Sprint> {
    Flux<Sprint> findByChannelId(String channelId, Pageable pageable);

    Flux<Sprint> findBySprintersContains(String sprinter, Pageable pageable);
}
