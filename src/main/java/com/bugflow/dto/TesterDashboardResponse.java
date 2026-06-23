package com.bugflow.dto;

public class TesterDashboardResponse {

    private long createdIssues;
    private long pendingVerification;
    private long verified;

    public long getCreatedIssues() {
        return createdIssues;
    }

    public void setCreatedIssues(long createdIssues) {
        this.createdIssues = createdIssues;
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