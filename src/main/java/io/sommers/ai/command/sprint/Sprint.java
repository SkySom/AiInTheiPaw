package io.sommers.ai.command.sprint;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.mapping.UpdateTime;
import io.sommers.ai.model.ProviderId;
import org.springframework.data.annotation.CreatedDate;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class Sprint {
    @DocumentId
    private String id;
    private Timestamp startTime;
    private Timestamp endTime;
    private ProviderId channelId;
    private SprintStatus status;
    private Map<String, Long> startingCounts;
    private Map<String, Long> endingCounts;
    private Timestamp createdAt;
    @UpdateTime(version = true)
    private Timestamp lastUpdated;

    public Sprint() {

    }

    public Sprint(
            Duration duration,
            ProviderId channelId
    ) {
        this.startTime = Timestamp.of(Date.from(Instant.now()
                .plus(Duration.of(1, ChronoUnit.MINUTES))
        ));
        this.endTime = Timestamp.of(Date.from(Instant.now()
                .plus(Duration.of(1, ChronoUnit.MINUTES))
                .plus(duration)
        ));
        this.channelId = channelId;
        this.status = SprintStatus.SIGN_UP;
        this.createdAt = Timestamp.now();
        this.startingCounts = new HashMap<>();
        this.endingCounts = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public ProviderId getChannelId() {
        return channelId;
    }

    public void setChannelId(ProviderId channelId) {
        this.channelId = channelId;
    }

    public SprintStatus getStatus() {
        return status;
    }

    public void setStatus(SprintStatus status) {
        this.status = status;
    }

    public Map<String, Long> getStartingCounts() {
        return startingCounts;
    }

    public void setStartingCounts(Map<String, Long> startingCounts) {
        this.startingCounts = startingCounts;
    }

    public Map<String, Long> getEndingCounts() {
        return endingCounts;
    }

    public void setEndingCounts(Map<String, Long> endingCounts) {
        this.endingCounts = endingCounts;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Sprint) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.startTime, that.startTime) &&
                Objects.equals(this.endTime, that.endTime) &&
                Objects.equals(this.channelId, that.channelId) &&
                Objects.equals(this.status, that.status) &&
                Objects.equals(this.startingCounts, that.startingCounts) &&
                Objects.equals(this.endingCounts, that.endingCounts) &&
                Objects.equals(this.createdAt, that.createdAt) &&
                Objects.equals(this.lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, endTime, channelId, status, startingCounts, endingCounts, createdAt, lastUpdated);
    }

    @Override
    public String toString() {
        return "Sprint[" +
                "id=" + id + ", " +
                "startTime=" + startTime + ", " +
                "endTime=" + endTime + ", " +
                "channelId=" + channelId + ", " +
                "status=" + status + ", " +
                "startingCounts=" + startingCounts + ", " +
                "endingCounts=" + endingCounts + ", " +
                "createdAt=" + createdAt + ", " +
                "lastUpdated=" + lastUpdated + ']';
    }

}
