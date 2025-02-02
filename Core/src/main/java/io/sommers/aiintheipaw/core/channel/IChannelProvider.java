package io.sommers.aiintheipaw.core.channel;

import reactor.core.publisher.Mono;

public interface IChannelProvider {

    Mono<IChannel> getChannel(String channelId);
}
