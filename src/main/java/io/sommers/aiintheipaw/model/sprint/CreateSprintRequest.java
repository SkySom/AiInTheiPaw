package io.sommers.aiintheipaw.model.sprint;

import jakarta.validation.constraints.Positive;

public record CreateSprintRequest(
        @Positive long channelId
) {

}
