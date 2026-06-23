package com.bugflow.dto;

public class DashboardStatsResponse {

    private String organizationName;

    private long totalProjects;

    private long totalMembers;

    private long totalIssues;

    private long assignedTasks;

    private long openIssues;

    private long inProgressIssues;

    private long reviewIssues;

    private long resolvedIssues;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(
            String organizationName) {
        this.organizationName = organizationName;
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

    public long getTotalIssues() {
        return totalIssues;
    }

    public void setTotalIssues(
            long totalIssues) {
        this.totalIssues = totalIssues;
    }

    public long getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(
            long assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public long getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(
            long openIssues) {
        this.openIssues = openIssues;
    }

    public long getInProgressIssues() {
        return inProgressIssues;
    }

    public void setInProgressIssues(
            long inProgressIssues) {
        this.inProgressIssues = inProgressIssues;
    }

    public long getReviewIssues() {
        return reviewIssues;
    }

    public void setReviewIssues(
            long reviewIssues) {
        this.reviewIssues = reviewIssues;
    }

    public long getResolvedIssues() {
        return resolvedIssues;
    }

    public void setResolvedIssues(
            long resolvedIssues) {
        this.resolvedIssues = resolvedIssues;
    }
}