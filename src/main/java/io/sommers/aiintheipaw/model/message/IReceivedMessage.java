package io.sommers.aiintheipaw.model.message;

import io.sommers.aiintheipaw.model.channel.IChannel;
import io.sommers.aiintheipaw.model.user.IUser;

public interface IReceivedMessage extends IMessage {
    String getId();

    IChannel getChannel();

    IUser getUser();
}
