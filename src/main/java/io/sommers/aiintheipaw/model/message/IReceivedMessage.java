package io.sommers.aiintheipaw.model.message;

import io.sommers.aiintheipaw.model.channel.IChannel;

public interface IReceivedMessage extends IMessage {
    IChannel getChannel();
}
