package com.bugflow.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "issues")
public class Issue {

    @Id
    private String id;

    private String projectId;
    private String projectName; 

    private String title;
    private String description;

    private String priority;
    private String status;
    private String assignedToName;
    private String assignedToUserId;
    private String assignedToEmail;
    private String assignedToRole;
    private String developerId;
    private String developerName;
    private String developerEmail;
    private String createdBy;
    private String testerId;
    private String testerName;
    private String testerEmail;
    private String imageUrl; 
    private String resolutionImageUrl;
    

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
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
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
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
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getResolutionImageUrl() {
		return resolutionImageUrl;
	}
	public void setResolutionImageUrl(String resolutionImageUrl) {
		this.resolutionImageUrl = resolutionImageUrl;
	}
	public String getAssignedToUserId() {
		return assignedToUserId;
	}
	public void setAssignedToUserId(String assignedToUserId) {
		this.assignedToUserId = assignedToUserId;
	}
	public String getAssignedToEmail() {
		return assignedToEmail;
	}
	public void setAssignedToEmail(String assignedToEmail) {
		this.assignedToEmail = assignedToEmail;
	}
	public String getAssignedToRole() {
		return assignedToRole;
	}
	public void setAssignedToRole(String assignedToRole) {
		this.assignedToRole = assignedToRole;
	}
	public String getAssignedToName() {
		return assignedToName;
	}
	public void setAssignedToName(String assignedToName) {
		this.assignedToName = assignedToName;
	}
	public String getDeveloperId() {
		return developerId;
	}
	public void setDeveloperId(String developerId) {
		this.developerId = developerId;
	}
	public String getDeveloperName() {
		return developerName;
	}
	public void setDeveloperName(String developerName) {
		this.developerName = developerName;
	}
	public String getDeveloperEmail() {
		return developerEmail;
	}
	public void setDeveloperEmail(String developerEmail) {
		this.developerEmail = developerEmail;
	}
	public String getTesterId() {
		return testerId;
	}
	public void setTesterId(String testerId) {
		this.testerId = testerId;
	}
	public String getTesterName() {
		return testerName;
	}
	public void setTesterName(String testerName) {
		this.testerName = testerName;
	}
	public String getTesterEmail() {
		return testerEmail;
	}
	public void setTesterEmail(String testerEmail) {
		this.testerEmail = testerEmail;
	}
	
	
	
    
}