package io.sommers.aiintheipaw.core.user.source;

import io.sommers.aiintheipaw.core.user.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(
        name = "user_source",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"service", "service_id"})
        }
)
@SuppressWarnings("unused")
public class UserSource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "service", nullable = false)
    private String service;
    @Column(name = "service_id", nullable = false)
    private String serviceId;
    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private Timestamp createdAt;
    @LastModifiedDate
    @Column(name = "updated_date", nullable = false)
    private Timestamp updatedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public UserSource() {

    }

    public UserSource(String service, String serviceId, User user) {
        this.service = service;
        this.serviceId = serviceId;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
