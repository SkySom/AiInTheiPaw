package io.sommers.aiintheipaw.model.channel;

import io.vavr.control.Option;

public interface IChannel {
    long getId();

    String getService();

    Option<String> getGuildId();

    String getChannelId();
}
