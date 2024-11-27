package io.sommers.ai.twitch.model;

import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.provider.IChannelProvider;
import reactor.core.publisher.Mono;

public class TwitchChannelProvider implements IChannelProvider {
    private final TwitchMessageService twitchMessageService;

    public TwitchChannelProvider(TwitchMessageService twitchMessageService) {
        this.twitchMessageService = twitchMessageService;
    }

    @Override
    public Mono<IChannel> getChannel(String channelId) {
        return Mono.just(new TwitchChannel(this.twitchMessageService, channelId));
    }
}
