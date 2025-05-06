package io.sommers.aiintheipaw.entity.sprint;

import io.sommers.aiintheipaw.entity.ChannelEntity;
import io.sommers.aiintheipaw.model.sprint.SprintStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "sprint")
@SuppressWarnings("unused")
public class SprintEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SprintStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "channel_id", nullable = false)
    private ChannelEntity channel;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "sprint_entry",
            joinColumns = @JoinColumn(name = "sprint_id")
    )
    private List<SprintEntryEntity> entries;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "sprint_status",
            joinColumns = @JoinColumn(name = "sprint_id")
    )
    private List<SprintStatusEntity> statuses;

    public SprintEntity() {

    }

    public SprintEntity(ChannelEntity channelEntity) {
        this.setStatus(SprintStatus.SIGN_UP);
        this.setChannel(channelEntity);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ChannelEntity getChannel() {
        return channel;
    }

    public void setChannel(ChannelEntity channel) {
        this.channel = channel;
    }

    public List<SprintEntryEntity> getEntries() {
        return entries;
    }

    public void setEntries(List<SprintEntryEntity> entries) {
        this.entries = entries;
    }

    public List<SprintStatusEntity> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<SprintStatusEntity> statuses) {
        this.statuses = statuses;
    }
}
