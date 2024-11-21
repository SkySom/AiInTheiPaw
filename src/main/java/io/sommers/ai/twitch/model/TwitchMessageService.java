package io.sommers.ai.twitch.model;

import com.github.twitch4j.helix.domain.ChatMessage;
import com.github.twitch4j.helix.domain.SentChatMessage;
import com.github.twitch4j.helix.domain.SentChatMessageWrapper;
import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.service.IMessageService;
import io.sommers.ai.twitch.TwitchConfiguration;
import io.sommers.ai.twitch.TwitchService;
import io.sommers.ai.model.IMessage;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class TwitchMessageService implements IMessageService {
    private final TwitchService twitchService;
    private final TwitchConfiguration twitchConfiguration;

    public TwitchMessageService(TwitchService twitchService, TwitchConfiguration twitchConfiguration) {
        this.twitchService = twitchService;
        this.twitchConfiguration = twitchConfiguration;
    }

    @Override
    public Mono<String> sendToChannel(IChannel channel, IMessage message) {
        return sendToChannel(channel, message, null);
    }

    @Override
    public Mono<String> sendToChannel(IChannel channel, IMessage message, @Nullable String replyTo) {
        return Mono.defer(() -> {
            SentChatMessageWrapper wrapper = this.twitchService.getTwitchClient()
                    .sendChatMessage(
                            null,
                            new ChatMessage(
                                    channel.getId().id(),
                                    twitchConfiguration.getBotId(),
                                    message.getText(),
                                    replyTo
                            )
                    )
                    .execute();

            return Mono.justOrEmpty(Optional.ofNullable(wrapper.get())
                    .map(SentChatMessage::getMessageId)
            );
        });
    }
}
