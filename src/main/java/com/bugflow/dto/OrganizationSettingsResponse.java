package com.bugflow.dto;

import java.time.LocalDateTime;

public class OrganizationSettingsResponse {

    private String id;

    private String name;

    private String ownerName;

    private LocalDateTime createdAt;

    private long totalProjects;

    private long totalMembers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(
            long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(
            long totalMembers) {
        this.totalMembers = totalMembers;
    }

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
}