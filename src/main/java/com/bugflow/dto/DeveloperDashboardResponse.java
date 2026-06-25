package com.bugflow.dto;

public class DeveloperDashboardResponse {

    private long totalProjects;
    private long assignedTasks;
    private long inProgress;
    
	public long getTotalProjects() {
		return totalProjects;
	}
	public void setTotalProjects(long totalProjects) {
		this.totalProjects = totalProjects;
	}
	public long getAssignedTasks() {
		return assignedTasks;
	}
	public void setAssignedTasks(long assignedTasks) {
		this.assignedTasks = assignedTasks;
	}
	public long getInProgress() {
		return inProgress;
	}
	public void setInProgress(long inProgress) {
		this.inProgress = inProgress;
	}        
}