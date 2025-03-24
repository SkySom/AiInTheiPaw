package io.sommers.aiintheipaw.model.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "user_source")
@SuppressWarnings("unused")
public class UserSourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String service;
    @Column(name = "service_user_id")
    private String serviceUserId;
    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


    public UserSourceEntity() {

    }

    public UserSourceEntity(UserEntity user, String service, String serviceUserId) {
        this.user = user;
        this.service = service;
        this.serviceUserId = serviceUserId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getServiceUserId() {
        return serviceUserId;
    }

    public void setServiceUserId(String serviceUserId) {
        this.serviceUserId = serviceUserId;
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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
