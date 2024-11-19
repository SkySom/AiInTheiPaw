package io.sommers.ai.twitch.model;

import io.sommers.ai.model.IChannel;
import io.sommers.ai.model.service.IChannelService;
import reactor.core.publisher.Mono;

public class TwitchChannelService implements IChannelService {
    private final TwitchMessageService twitchMessageService;

    public TwitchChannelService(TwitchMessageService twitchMessageService) {
        this.twitchMessageService = twitchMessageService;
    }

    @Override
    public Mono<IChannel> getChannel(String channelId) {
        return Mono.just(new TwitchChannel(this.twitchMessageService, channelId));
    }
}
