package io.sommers.ai.twitch.model;

import io.sommers.ai.model.IChannel;
import io.sommers.ai.model.IMessage;
import reactor.core.publisher.Mono;

public class TwitchChannel implements IChannel {
    private final TwitchMessageService twitchMessageService;
    private final String broadcasterId;

    public TwitchChannel(TwitchMessageService twitchMessageService, String broadcasterId) {
        this.twitchMessageService = twitchMessageService;
        this.broadcasterId = broadcasterId;
    }

    @Override
    public Mono<String> replyTo(IMessage message) {
        return this.twitchMessageService.sendToChannel(
                this,
                message
        );
    }

    @Override
    public String getId() {
        return this.broadcasterId;
    }

    @Override
    public String getService() {
        return "twitch";
    }
}
