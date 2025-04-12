package io.sommers.aiintheipaw.model.twitch.message;

import com.fasterxml.jackson.annotation.JsonGetter;
import io.sommers.aiintheipaw.validation.NullOrNotBlank;
import jakarta.validation.constraints.NotBlank;

public record SendTwitchMessageRequest(
        @NotBlank @JsonGetter(value = "broadcaster_id") String broadcasterId,
        @NotBlank @JsonGetter(value = "sender_id") String senderId,
        @NullOrNotBlank @JsonGetter(value = "reply_parent_message_id") String replyParentMessageId,
        @NotBlank @JsonGetter(value = "message") String message
) {

}
