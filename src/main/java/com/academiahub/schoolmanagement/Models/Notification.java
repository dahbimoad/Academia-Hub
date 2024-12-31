package com.academiahub.schoolmanagement.Models;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String title;
    private String message;
    private String type;
    private String recipientRole;
    private boolean readStatus;
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(String title, String message, boolean readStatus, String recipientRole, String type, LocalDateTime createdAt) {
        this.title = title;
        this.message = message;
        this.readStatus = readStatus;
        this.recipientRole = recipientRole;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecipientRole() {
        return recipientRole;
    }

    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    @Override
public String toString() {
    return "Notification{id=" + id +
           ", title='" + title + '\'' +
           ", message='" + message + '\'' +
           ", type='" + type + '\'' +
           ", recipientRole='" + recipientRole + '\'' +
           ", readStatus=" + readStatus +
           ", createdAt=" + createdAt +
           '}';
}

}