package io.sommers.ai.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import io.sommers.ai.model.sprint.Sprint;
import io.sommers.ai.model.sprint.SprintStatus;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Collection;

@Repository
public interface ISprintRepository extends FirestoreReactiveRepository<Sprint> {
    Flux<Sprint> findByStatusInOrderByLastUpdated(Collection<SprintStatus> statuses);
}
