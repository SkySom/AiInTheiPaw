package io.sommers.aiintheipaw.logic;

import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.model.user.IUser;
import io.sommers.aiintheipaw.model.user.User;
import io.sommers.aiintheipaw.model.user.UserEntity;
import io.sommers.aiintheipaw.model.user.UserSourceEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import java.util.Set;

@ApplicationScoped
public class UserSourceLogic {
    @Inject
    SessionFactory sessionFactory;

    @CacheResult(cacheName = "user")
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
                    .transformToUni(userSourceEntity -> {
                        if (userSourceEntity == null) {
                            UserEntity newUser = new UserEntity();

                            return session.persist(newUser)
                                    .flatMap(ignoredUser -> {
                                        UserSourceEntity newUserSourceEntity = new UserSourceEntity(
                                                newUser,
                                                service,
                                                userServiceId
                                        );

                                        return session.persist(newUserSourceEntity)
                                                .map(ignoredSource -> {
                                                    newUser.setUserSources(Set.of(newUserSourceEntity));
                                                    return newUserSourceEntity;
                                                });
                                    });
                        } else {
                            return Uni.createFrom()
                                    .item(userSourceEntity);
                        }
                    })
                    .map(userSourceEntity -> new User(
                            userSourceEntity.getUser()
                    ));
        });
    }
}
