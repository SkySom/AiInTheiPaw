package io.sommers.aiintheipaw.logic;

import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.model.service.IService;
import io.sommers.aiintheipaw.model.user.IUser;
import io.sommers.aiintheipaw.model.user.User;
import io.sommers.aiintheipaw.entity.UserEntity;
import io.sommers.aiintheipaw.entity.UserSourceEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

@ApplicationScoped
public class UserSourceLogic {
    @Inject
    SessionFactory sessionFactory;

    //@CacheResult(cacheName = "user")
    public Uni<IUser> findByServiceAndId(IService service, String userServiceId) {
        return sessionFactory.withSession(session -> {
            CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
            CriteriaQuery<UserSourceEntity> query = criteriaBuilder.createQuery(UserSourceEntity.class);

            Root<UserSourceEntity> root = query.from(UserSourceEntity.class);
            query.where(
                    criteriaBuilder.equal(root.get("service"), service),
                    criteriaBuilder.equal(root.get("serviceUserId"), userServiceId)
            );

            return session.createQuery(query)
                    .getSingleResultOrNull()
                    .onItem()
                    .ifNull()
                    .switchTo(() -> createNewUser(session, service, userServiceId))
                    .map(userSourceEntity -> new User(
                            userSourceEntity.getUser()
                    ));
        });
    }

    private Uni<UserSourceEntity> createNewUser(Session session, IService service, String userServiceId) {
        UserEntity newUser = new UserEntity();
        UserSourceEntity userSourceEntity = new UserSourceEntity(
                newUser,
                service,
                userServiceId
        );

        return session.persist(userSourceEntity)
                .replaceWith(userSourceEntity);
    }
}
