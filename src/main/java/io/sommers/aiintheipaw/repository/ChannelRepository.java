package io.sommers.aiintheipaw.repository;

import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.entity.ChannelEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

@ApplicationScoped
public class ChannelRepository {
    @Inject
    SessionFactory sessionFactory;

    @CacheResult(cacheName = "channel")
    public Uni<ChannelEntity> getById(Long id) {
        return sessionFactory.withSession(session -> session.find(ChannelEntity.class, id));
    }
}
