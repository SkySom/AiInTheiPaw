package io.sommers.ai.model.channel;

import io.sommers.ai.model.ProviderId;

public record ChannelDataValue(
        ProviderId channelId,
        String name,
        Object value
) {
}
