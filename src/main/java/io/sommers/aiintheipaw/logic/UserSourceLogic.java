package io.sommers.aiintheipaw.logic;

import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.model.user.IUser;
import io.sommers.aiintheipaw.model.user.User;
import io.sommers.aiintheipaw.model.user.UserEntity;
import io.sommers.aiintheipaw.model.user.UserSourceEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.*;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import java.util.Set;

@ApplicationScoped
public class UserSourceLogic {
    @Inject
    SessionFactory sessionFactory;

    //@CacheResult(cacheName = "user")
    public Uni<IUser> findByServiceAndId(String service, String id) {
        return sessionFactory.withSession(session -> {
            CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
            CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);

            Root<UserEntity> userEntityRoot = query.from(UserEntity.class);
            Root<UserSourceEntity> userSourceEntityRoot = query.from(UserSourceEntity.class);

            Subquery<Long> userSourceQuery = query.subquery(Long.class)
                    .select(userSourceEntityRoot.get("id"))
                    .where(
                            criteriaBuilder.equal(userSourceEntityRoot.get("service"), service),
                            criteriaBuilder.equal(userSourceEntityRoot.get("id"), id)
                    );


            query.where(
                    criteriaBuilder.equal(userEntityRoot.get("id"), userSourceQuery.getSelection())
            ).select(userEntityRoot);

            return session.createQuery(query)
                    .getSingleResultOrNull()
                    .onItem()
                    .transformToUni(userEntity -> {
                        if (userEntity == null) {
                            UserEntity newUser = new UserEntity();

                            return session.persist(newUser)
                                    .flatMap(ignoredUser -> {
                                        UserSourceEntity userSourceEntity = new UserSourceEntity(
                                                newUser,
                                                service,
                                                id
                                        );

                                        return session.persist(userSourceEntity)
                                                .map(ignoredSource -> {
                                                    newUser.setUserSources(Set.of(userSourceEntity));
                                                    return newUser;
                                                });
                                    });
                        } else {
                            return Uni.createFrom()
                                    .item(userEntity);
                        }
                    })
                    .map(userEntity -> new User(
                            userEntity.getId()
                    ));
        });
    }
}
