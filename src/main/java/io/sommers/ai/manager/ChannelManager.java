package io.sommers.ai.manager;

import io.sommers.ai.model.IChannel;
import io.sommers.ai.model.service.IChannelService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ChannelManager {
    private final Map<String, IChannelService> channelServices;

    public ChannelManager(Map<String, IChannelService> channelServices) {
        this.channelServices = channelServices;
    }

    public Mono<IChannel> getChannel(String service, String channelName) {
        IChannelService channelService = channelServices.get(service);

        if (channelService != null) {
            return channelService.getChannel(channelName);
        } else {
            return Mono.empty();
        }
    }
}
