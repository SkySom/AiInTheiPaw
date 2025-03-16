package io.sommers.aiintheipaw.core.user.source;

import io.sommers.aiintheipaw.core.user.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Table(name = "user_source")
@SuppressWarnings("unused")
public class UserSource {
    @Id
    private UUID id;
    private String service;
    private String serviceId;
    @CreatedDate
    private Timestamp createdDate;
    @LastModifiedDate
    private Timestamp updatedDate;

    public UserSource() {

    }

    public UserSource(String service, String serviceId) {
        this.service = service;
        this.serviceId = serviceId;
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

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }
}
