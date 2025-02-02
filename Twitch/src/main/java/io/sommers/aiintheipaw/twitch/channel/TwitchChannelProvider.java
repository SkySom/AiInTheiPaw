package io.sommers.aiintheipaw.twitch.channel;

import io.sommers.aiintheipaw.core.channel.IChannel;
import io.sommers.aiintheipaw.core.channel.IChannelProvider;
import io.sommers.aiintheipaw.twitch.message.TwitchMessageService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
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
