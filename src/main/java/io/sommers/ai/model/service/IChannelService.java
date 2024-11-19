package io.sommers.ai.model.service;

import io.sommers.ai.model.IChannel;
import reactor.core.publisher.Mono;

public interface IChannelService {

    Mono<IChannel> getChannel(String channelId);
}
