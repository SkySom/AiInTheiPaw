package io.sommers.aiintheipaw.model.channel;

import io.vavr.control.Option;
import jakarta.annotation.Nullable;

public record Channel(
        long id,
        String service,
        @Nullable String guildId,
        String channelId
) implements IChannel {

    @Override
    public long getId() {
        return this.id();
    }

    @Override
    public String getService() {
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
