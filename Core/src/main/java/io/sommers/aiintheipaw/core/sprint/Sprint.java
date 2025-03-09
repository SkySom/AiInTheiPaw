package io.sommers.aiintheipaw.core.sprint;

import io.sommers.aiintheipaw.core.util.ProviderId;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@SuppressWarnings("unused")
public class Sprint {
    private UUID id;
    private Timestamp startTime;
    private Timestamp endTime;
    private String channelId;
    private SprintStatus status;
    private Timestamp createdAt;
    private Timestamp lastUpdated;

    public Sprint() {

    }

    public Sprint(
            Duration signUpDuration,
            Duration inProgressDuration,
            ProviderId channelId
    ) {
        this.startTime = Timestamp.from(Instant.now()
                .plus(signUpDuration)
        );
        this.endTime = Timestamp.from(Instant.now()
                .plus(signUpDuration)
                .plus(inProgressDuration)
        );
        this.channelId = channelId.asDocumentKey();
        this.status = SprintStatus.SIGN_UP;
        this.createdAt = Timestamp.from(Instant.now());
        this.lastUpdated = Timestamp.from(Instant.now());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public SprintStatus getStatus() {
        return status;
    }

    public void setStatus(SprintStatus status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
