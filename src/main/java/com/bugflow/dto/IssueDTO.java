package com.bugflow.dto;

public class IssueDTO {

    private String title;
    private String description;
    private String priority;
    private String assignedToName;
    private String assignedToUserId;
    private String assignedToEmail;
    private String assignedToRole;
    private String projectName;

    private String status;
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
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAssignedToName() {
		return assignedToName;
	}
	public void setAssignedToName(String assignedToName) {
		this.assignedToName = assignedToName;
	}
    
    
}
