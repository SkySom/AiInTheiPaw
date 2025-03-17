package io.sommers.aiintheipaw.model.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Map;

@Entity
@SuppressWarnings("unused")
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;
    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "user_source",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
    )
    @MapKey(name = "service")
    private Map<String, UserSourceEntity> userSources;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Map<String, UserSourceEntity> getUserSources() {
        return userSources;
    }

    public void setUserSources(Map<String, UserSourceEntity> userSources) {
        this.userSources = userSources;
    }
}
