package io.sommers.aiintheipaw.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@SuppressWarnings("unused")
public class SendMessageRequest {
    @NotNull
    @Positive
    private Long channelId;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
