package io.sommers.ai.model.sprint;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.mapping.UpdateTime;
import io.sommers.ai.model.ProviderId;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@SuppressWarnings("unused")
public class Sprint {
    @DocumentId
    private String documentId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String channelId;
    private SprintStatus status;
    private List<String> sprinters;
    private Map<String, Long> startingCounts;
    private Map<String, Long> endingCounts;
    private Timestamp createdAt;
    @UpdateTime(version = true)
    private Timestamp lastUpdated;

    public Sprint() {

    }

    public Sprint(
            Duration signUpDuration,
            Duration inProgressDuration,
            ProviderId channelId
    ) {
        this.startTime = Timestamp.of(Date.from(Instant.now()
                .plus(signUpDuration)
        ));
        this.endTime = Timestamp.of(Date.from(Instant.now()
                .plus(signUpDuration)
                .plus(inProgressDuration)
        ));
        this.channelId = channelId.asDocumentKey();
        this.status = SprintStatus.SIGN_UP;
        this.createdAt = Timestamp.now();
        this.sprinters = new ArrayList<>();
        this.startingCounts = new HashMap<>();
        this.endingCounts = new HashMap<>();
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public List<String> getSprinters() {
        return sprinters;
    }

    public void setSprinters(List<String> sprinters) {
        this.sprinters = sprinters;
    }

    public void addSprinter(String sprinter) {
        if (this.getSprinters() == null) {
            this.setSprinters(new ArrayList<>());
        }
        if (!this.getSprinters().contains(sprinter)) {
            this.getSprinters().add(sprinter);
        }
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
        return Objects.equals(this.documentId, that.documentId) &&
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
        return Objects.hash(documentId, startTime, endTime, channelId, status, startingCounts, endingCounts, createdAt, lastUpdated);
    }

    @Override
    public String toString() {
        return "Sprint[" +
                "id=" + documentId + ", " +
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
