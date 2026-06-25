package com.bugflow.dto;

public class TesterDashboardResponse {

    private long assignedIssues;
    private long pendingVerification;
    private long verified;
    

    // getters setters
    
    
	public long getAssignedIssues() {
		return assignedIssues;
	}
	public void setAssignedIssues(long assignedIssues) {
		this.assignedIssues = assignedIssues;
	}
	public long getPendingVerification() {
		return pendingVerification;
	}
	public void setPendingVerification(long pendingVerification) {
		this.pendingVerification = pendingVerification;
	}
	public long getVerified() {
		return verified;
	}
	public void setVerified(long verified) {
		this.verified = verified;
	}

}
