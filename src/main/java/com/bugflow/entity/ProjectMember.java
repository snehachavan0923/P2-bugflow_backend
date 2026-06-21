package com.bugflow.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "project_members")
public class ProjectMember {

    @Id
    private String id;

    private String projectId;

    private String userId;

    private String name;

    private String email;

    private Role role;

    private LocalDateTime joinedAt;

    private String organizationId;
    
    public ProjectMember() {
        this.joinedAt = LocalDateTime.now();
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}

	public String getOrganizationId() {
	    return organizationId;
	}

	public void setOrganizationId(
	        String organizationId) {
	    this.organizationId = organizationId;
	}
}