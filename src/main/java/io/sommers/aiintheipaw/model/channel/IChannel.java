package io.sommers.aiintheipaw.model.channel;

import io.sommers.aiintheipaw.model.service.IService;
import io.vavr.control.Option;

public interface IChannel {
    long getId();

    IService getService();

    Option<String> getGuildId();

    String getChannelId();
}
