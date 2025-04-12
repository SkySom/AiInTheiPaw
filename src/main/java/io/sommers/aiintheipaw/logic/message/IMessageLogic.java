package io.sommers.aiintheipaw.logic.message;

import io.smallrye.mutiny.Uni;
import io.sommers.aiintheipaw.model.channel.IChannel;
import io.sommers.aiintheipaw.model.message.IMessage;
import io.sommers.aiintheipaw.model.message.IReceivedMessage;
import io.sommers.aiintheipaw.model.service.IService;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface IMessageLogic {

    Uni<IMessage> sendMessage(@NotNull IChannel channel, @Nullable String replyToId, @NotBlank String message);

    IService getService();
}
