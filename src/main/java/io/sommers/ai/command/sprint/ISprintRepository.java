package io.sommers.ai.command.sprint;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISprintRepository extends FirestoreReactiveRepository<Sprint> {
}
