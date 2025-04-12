package io.sommers.aiintheipaw.logic.message;

import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.http.ServiceCallException;
import io.sommers.aiintheipaw.http.TwitchRestClient;
import io.sommers.aiintheipaw.model.channel.IChannel;
import io.sommers.aiintheipaw.model.message.BotMessage;
import io.sommers.aiintheipaw.model.message.IMessage;
import io.sommers.aiintheipaw.model.service.IService;
import io.sommers.aiintheipaw.model.twitch.message.SendTwitchMessageRequest;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;

@ApplicationScoped
public class TwitchMessageLogic implements IMessageLogic {
    @RestClient
    TwitchRestClient twitchRestClient;

    @Inject
    IService twitch;

    @ConfigProperty(name = "twitch.bot-id")
    String twitchBotId;

    @Override
    public Uni<IMessage> sendMessage(@NotNull IChannel channel, @Nullable String replyTo, @NotBlank String message) {
        if (channel.getService() != this.twitch) {
            return Uni.createFrom()
                    .failure(new IllegalArgumentException("Channel is not a Twitch channel"));
        } else {
            return this.twitchRestClient.sendMessage(new SendTwitchMessageRequest(
                            channel.getChannelId(),
                            twitchBotId,
                            replyTo,
                            message
                    ))
                    .flatMap(response -> Uni.createFrom()
                            .optional(response.data()
                                    .stream()
                                    .findFirst()
                            )
                    )
                    .flatMap(response -> {
                        if (response.isSent()) {
                            //TODO include id
                            return Uni.createFrom()
                                    .item(new BotMessage(message));
                        } else if (response.getDropReason() != null) {
                            return Uni.createFrom()
                                    .failure(new ServiceCallException(twitch, Map.of("dropReason", response.getDropReason().getMessage())));
                        } else {
                            return Uni.createFrom()
                                    .failure(new IllegalStateException("Failed to send message"));
                        }
                    });
        }
    }

    @Override
    public IService getService() {
        return this.twitch;
    }
}
