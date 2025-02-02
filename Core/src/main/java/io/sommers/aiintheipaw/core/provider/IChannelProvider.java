package io.sommers.aiintheipaw.core.provider;

import io.sommers.ai.model.channel.IChannel;
import reactor.core.publisher.Mono;

public interface IChannelProvider {

    Mono<IChannel> getChannel(String channelId);
}
