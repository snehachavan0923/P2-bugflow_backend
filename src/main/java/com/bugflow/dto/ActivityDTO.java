package com.bugflow.dto;

import java.time.LocalDateTime;

public class ActivityDTO {
    
    private String type; // ORGANIZATION, USER, SUBSCRIPTION, PAYMENT, PROJECT
    private String title;
    private String description;
    private String userName;
    private LocalDateTime createdAt;
    private String icon; // Icon type for frontend display

    public ActivityDTO() {
    }

    public ActivityDTO(String type, String title, String description, 
                       String userName, LocalDateTime createdAt, String icon) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.userName = userName;
        this.createdAt = createdAt;
        this.icon = icon;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
