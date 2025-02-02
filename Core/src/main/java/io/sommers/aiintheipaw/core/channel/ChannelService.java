package io.sommers.aiintheipaw.core.channel;

import io.sommers.aiintheipaw.core.util.ProviderId;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ChannelService {
    private final Map<String, IChannelProvider> channelServices;

    public ChannelService(Map<String, IChannelProvider> channelServices) {
        this.channelServices = channelServices;
    }

    public Mono<IChannel> getChannel(ProviderId providerId) {
        IChannelProvider channelService = channelServices.get(providerId.provider());

        if (channelService != null) {
            return channelService.getChannel(providerId.id());
        } else {
            return Mono.error(new IllegalArgumentException("No channel found for provider id " + providerId.id()));
        }
    }
}
