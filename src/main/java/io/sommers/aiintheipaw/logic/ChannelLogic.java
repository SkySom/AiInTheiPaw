package io.sommers.aiintheipaw.logic;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.model.channel.Channel;
import io.sommers.aiintheipaw.model.channel.ChannelEntity;
import io.sommers.aiintheipaw.model.channel.IChannel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

@ApplicationScoped
public class ChannelLogic {

    @Inject
    SessionFactory sessionFactory;

    @CacheResult(cacheName = "channel")
    public Uni<IChannel> findByServiceGuildIdAndChannelId(@CacheKey String service, @CacheKey String guildId, @CacheKey String channelId) {
        return sessionFactory.withSession(session -> {
            CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
            CriteriaQuery<ChannelEntity> query = sessionFactory.getCriteriaBuilder()
                    .createQuery(ChannelEntity.class);

            Root<ChannelEntity> channelEntityRoot = query.from(ChannelEntity.class);

            query.where(
                    criteriaBuilder.equal(channelEntityRoot.get("service"), service)
            ).select(channelEntityRoot);

            return session.createQuery(query)
                    .getSingleResultOrNull()
                    .onItem()
                    .transformToUni(channel -> {
                        if (channel == null) {
                            ChannelEntity newChannel = new ChannelEntity();
                            newChannel.setChannelId(channelId);
                            newChannel.setService(service);
                            newChannel.setGuildId(guildId);

                            return session.persist(newChannel)
                                    .map(ignored -> newChannel);
                        } else {
                            return Uni.createFrom()
                                    .item(channel);
                        }
                    })
                    .map(channelEntity -> new Channel(
                            channelEntity.getId(),
                            channelEntity.getService(),
                            channelEntity.getGuildId(),
                            channelEntity.getChannelId()
                    ));
        });
    }
}
