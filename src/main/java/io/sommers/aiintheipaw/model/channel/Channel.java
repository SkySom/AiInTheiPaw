package io.sommers.aiintheipaw.model.channel;

import io.sommers.aiintheipaw.model.service.IService;
import io.vavr.control.Option;
import jakarta.annotation.Nullable;

public record Channel(
        long id,
        IService service,
        @Nullable String guildId,
        String channelId
) implements IChannel {

    @Override
    public long getId() {
        return this.id();
    }

    @Override
    public IService getService() {
        return this.service();
    }

    @Override
    public Option<String> getGuildId() {
        return Option.of(this.guildId());
    }

    @Override
    public String getChannelId() {
        return this.channelId();
    }
}
