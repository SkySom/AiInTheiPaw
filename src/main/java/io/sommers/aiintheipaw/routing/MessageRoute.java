package io.sommers.aiintheipaw.routing;

import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.logic.ChannelLogic;
import io.sommers.aiintheipaw.logic.message.IMessageLogic;
import io.sommers.aiintheipaw.model.message.IMessage;
import io.sommers.aiintheipaw.model.request.SendMessageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@ApplicationScoped
@Path("bot")
public class MessageRoute {
    @Inject
    ChannelLogic channelLogic;

    @Inject
    Instance<IMessageLogic> messageLogics;

    @POST
    @Path("message")
    public Uni<IMessage> sendMessage(@Valid SendMessageRequest sendMessageRequest) {
        return this.channelLogic.getById(sendMessageRequest.getChannelId())
                .flatMap(channel -> {
                    IMessageLogic messageLogic = messageLogics.stream()
                            .filter(value -> value.getService()
                                    .equals(channel.getService())
                            )
                            .findAny()
                            .orElse(null);

                    if (messageLogic == null) {
                        return Uni.createFrom()
                                .failure(new IllegalStateException("No message logic found for " + channel.getService()));
                    } else {
                        return messageLogic.sendMessage(channel, sendMessageRequest.getReplyToId(), sendMessageRequest.getMessage());
                    }
                });
    }
}
