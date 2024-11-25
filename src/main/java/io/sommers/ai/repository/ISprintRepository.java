package io.sommers.ai.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import io.sommers.ai.model.sprint.Sprint;
import org.springframework.stereotype.Repository;

@Repository
public interface ISprintRepository extends FirestoreReactiveRepository<Sprint> {
}
