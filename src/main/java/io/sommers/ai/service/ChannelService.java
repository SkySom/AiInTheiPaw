package io.sommers.ai.service;

import io.sommers.ai.model.ProviderId;
import io.sommers.ai.model.channel.ChannelDataValue;
import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.repository.IChannelDataRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ChannelService {
    private final Map<String, IChannelProvider> channelServices;

    private final IChannelDataRepository channelDataRepository;

    public ChannelService(Map<String, IChannelProvider> channelServices, IChannelDataRepository channelDataRepository) {
        this.channelServices = channelServices;
        this.channelDataRepository = channelDataRepository;
    }

    public Mono<IChannel> getChannel(ProviderId providerId) {
        IChannelProvider channelService = channelServices.get(providerId.provider());

        if (channelService != null) {
            return channelService.getChannel(providerId.id());
        } else {
            return Mono.error(new IllegalArgumentException("No channel found for provider id " + providerId.id()));
        }
    }

    @Cacheable(value = "channelDataValue")
    public Mono<ChannelDataValue> getChannelDataValue(ProviderId channelId, String name) {
        return this.channelDataRepository.findById(channelId.asDocumentKey())
                .flatMap(channelData -> {
                    Object value = channelData.dataValues()
                            .get(name);

                    if (value != null) {
                        return Mono.just(new ChannelDataValue(
                                channelData.channelId(),
                                name,
                                value
                        ));
                    } else {
                        return Mono.empty();
                    }
                });
    }
}
