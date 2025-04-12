package io.sommers.aiintheipaw.model.request;

import io.sommers.aiintheipaw.validation.NullOrNotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@SuppressWarnings("unused")
public class SendMessageRequest {
    @NotNull
    @Positive
    private Long channelId;
    @NullOrNotBlank
    private String replyToId;
    @NotEmpty
    private String message;

    public SendMessageRequest() {

    }

    public SendMessageRequest(@NotNull Long channelId, String replyToId, @NotNull String message) {
        this.channelId = channelId;
        this.replyToId = replyToId;
        this.message = message;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(String replyToId) {
        this.replyToId = replyToId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
