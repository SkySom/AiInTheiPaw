package io.sommers.aiintheipaw.entity.sprint;

import io.sommers.aiintheipaw.model.sprint.SprintStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "sprint_status")
public class SprintStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "new_status")
    @Enumerated(EnumType.STRING)
    private SprintStatus newStatus;
    @Column(name = "previous_status")
    @Enumerated(EnumType.STRING)
    private SprintStatus previousStatus;
    @Column(name = "next_update")
    private Timestamp nextUpdate;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sprint_id", nullable = false)
    private SprintEntity sprint;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SprintStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(SprintStatus newStatus) {
        this.newStatus = newStatus;
    }

    public SprintStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(SprintStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public Timestamp getNextUpdate() {
        return nextUpdate;
    }

    public void setNextUpdate(Timestamp nextUpdate) {
        this.nextUpdate = nextUpdate;
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

    public SprintEntity getSprint() {
        return sprint;
    }

    public void setSprint(SprintEntity sprint) {
        this.sprint = sprint;
    }
}
