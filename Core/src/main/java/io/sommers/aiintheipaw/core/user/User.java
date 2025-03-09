package io.sommers.aiintheipaw.core.user;

import io.sommers.aiintheipaw.core.user.source.UserSource;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Table(name = "user")
@Entity
@SuppressWarnings("unused")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @CreatedDate
    private Timestamp createdAt;
    @LastModifiedDate
    private Timestamp updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "user_source",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
    )
    @MapKey(name = "service")
    private Map<String, UserSource> userSources;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
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

    public Map<String, UserSource> getUserSources() {
        return userSources;
    }

    public void setUserSources(Map<String, UserSource> userSources) {
        this.userSources = userSources;
    }
}
