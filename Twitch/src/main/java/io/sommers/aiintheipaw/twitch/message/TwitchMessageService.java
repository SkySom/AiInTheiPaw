package io.sommers.aiintheipaw.twitch.message;

import com.github.twitch4j.helix.domain.ChatMessage;
import com.github.twitch4j.helix.domain.SentChatMessage;
import com.github.twitch4j.helix.domain.SentChatMessageWrapper;
import io.sommers.aiintheipaw.core.channel.IChannel;
import io.sommers.aiintheipaw.core.message.IMessage;
import io.sommers.aiintheipaw.core.message.IMessageService;
import io.sommers.aiintheipaw.core.messagebuilder.MessageBuilder;
import io.sommers.aiintheipaw.twitch.TwitchConfiguration;
import io.sommers.aiintheipaw.twitch.TwitchService;
import io.sommers.aiintheipaw.twitch.user.TwitchUserSourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class TwitchMessageService implements IMessageService {
    private final TwitchService twitchService;
    private final TwitchConfiguration twitchConfiguration;
    private final TwitchUserSourceProvider twitchUserProvider;
    private final MessageSource messageSource;

    public TwitchMessageService(TwitchService twitchService, TwitchConfiguration twitchConfiguration,
                                TwitchUserSourceProvider twitchUserProvider, MessageSource messageSource) {
        this.twitchService = twitchService;
        this.twitchConfiguration = twitchConfiguration;
        this.twitchUserProvider = twitchUserProvider;
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
        return this.sendToChannel(channel, messageBuilder.build(this::getMessageBuilder, this.messageSource::getMessage));
    }

    @Override
    public Mono<String> sendToChannel(IChannel channel, @NotNull MessageBuilder messageBuilder, @Nullable String replyTo) {
        return this.sendToChannel(channel, messageBuilder.build(this::getMessageBuilder, this.messageSource::getMessage), replyTo);
    }

    @Override
    public MessageBuilder getMessageBuilder() {
        return new TwitchMessageBuilder(this.twitchConfiguration, this.twitchUserProvider);
    }
}
