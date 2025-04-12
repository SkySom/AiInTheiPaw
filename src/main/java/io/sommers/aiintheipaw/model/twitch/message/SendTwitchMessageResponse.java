package io.sommers.aiintheipaw.model.twitch.message;

import com.fasterxml.jackson.annotation.JsonSetter;
import io.sommers.aiintheipaw.validation.NullOrNotBlank;

public class SendTwitchMessageResponse {
    @NullOrNotBlank
    private String messageId;
    private boolean isSent;
    private DropReason dropReason;

    public String getMessageId() {
        return messageId;
    }

    @JsonSetter(value = "message_id")
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isSent() {
        return isSent;
    }

    @JsonSetter(value = "is_sent")
    public void setSent(boolean sent) {
        isSent = sent;
    }

    public DropReason getDropReason() {
        return dropReason;
    }

    @JsonSetter(value = "drop_reason")
    public void setDropReason(DropReason dropReason) {
        this.dropReason = dropReason;
    }
}
