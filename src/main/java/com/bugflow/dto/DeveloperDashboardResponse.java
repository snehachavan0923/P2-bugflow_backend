package com.bugflow.dto;

public class DeveloperDashboardResponse {

    private long assignedTasks;
    private long inProgress;
    private long review;

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

    public long getReview() {
        return review;
    }

    public void setReview(long review) {
        this.review = review;
    }
}