package io.sommers.ai.model.channel;

import com.google.cloud.Timestamp;
import com.google.cloud.spring.data.firestore.mapping.UpdateTime;
import io.sommers.ai.model.ProviderId;

import java.util.Map;

public record ChannelData(
        ProviderId channelId,
        Map<String, ChannelDataValue> dataValues,
        @UpdateTime(version = true)
        Timestamp timestamp
) {
}
