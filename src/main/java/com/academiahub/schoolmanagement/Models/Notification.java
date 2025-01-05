package com.academiahub.schoolmanagement.Models;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String message;
    private String type; // "ALERT" ou "REMINDER"
    private LocalDateTime dateCreation;
    private boolean isRead;

    public Notification(int id, String message, String type, LocalDateTime dateCreation, boolean isRead) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.dateCreation = dateCreation;
        this.isRead = isRead;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}