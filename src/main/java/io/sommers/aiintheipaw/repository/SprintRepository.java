package io.sommers.aiintheipaw.repository;

import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.entity.sprint.SprintEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

@ApplicationScoped
public class SprintRepository {
    @Inject
    SessionFactory sessionFactory;

    public Uni<SprintEntity> persistSprint(SprintEntity sprintEntity) {
        return sessionFactory.withSession(session -> session.persist(sprintEntity)
                .replaceWith(sprintEntity)
        );
    }
}
