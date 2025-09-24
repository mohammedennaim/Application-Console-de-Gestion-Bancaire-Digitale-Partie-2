package org.bank.domain;

import java.time.LocalDateTime;

public class Historique {
    public enum Action
    {
        DEPOSIT, CLOSE_ACCOUNT, REQUEST_CREDIT
    }
    public enum EntityType
    {
        ACCOUNT, TRANSACTION, CREDIT
    }
    private long id;
    private LocalDateTime timestamp;
    private long userId;
    private Action action;
    private EntityType entityType;
    private String entityId;
    private String details;

    public Historique() {}

    public Historique(long id, LocalDateTime timestamp, long userId, Action action, EntityType entityType, String entityId, String details) {
        this.id = id;
        this.timestamp = timestamp == null ? LocalDateTime.now() : timestamp;
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public long getUserId() {
        return userId;
    }

    public Action getAction() {
        return action;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}