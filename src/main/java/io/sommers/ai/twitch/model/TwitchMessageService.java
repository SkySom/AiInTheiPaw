package io.sommers.ai.twitch.model;

import com.github.twitch4j.helix.domain.ChatMessage;
import com.github.twitch4j.helix.domain.SentChatMessage;
import com.github.twitch4j.helix.domain.SentChatMessageWrapper;
import io.sommers.ai.model.channel.IChannel;
import io.sommers.ai.model.messagebuilder.MessageBuilder;
import io.sommers.ai.service.IMessageService;
import io.sommers.ai.twitch.TwitchConfiguration;
import io.sommers.ai.twitch.TwitchService;
import io.sommers.ai.model.IMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSource;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class TwitchMessageService implements IMessageService {
    private final TwitchService twitchService;
    private final TwitchConfiguration twitchConfiguration;
    private final MessageSource messageSource;

    public TwitchMessageService(TwitchService twitchService, TwitchConfiguration twitchConfiguration,
                                MessageSource messageSource) {
        this.twitchService = twitchService;
        this.twitchConfiguration = twitchConfiguration;
        this.messageSource = messageSource;
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

    @Override
    public Mono<String> sendToChannel(IChannel channel, @NotNull MessageBuilder messageBuilder) {
        return this.sendToChannel(channel, messageBuilder.build(this.messageSource));
    }

    @Override
    public Mono<String> sendToChannel(IChannel channel, @NotNull MessageBuilder messageBuilder, @Nullable String replyTo) {
        return this.sendToChannel(channel, messageBuilder.build(this.messageSource), replyTo);
    }

    @Override
    public MessageBuilder getMessageBuilder() {
        return new TwitchMessageBuilder(this.twitchConfiguration);
    }
}
