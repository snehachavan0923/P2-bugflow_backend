package com.bugflow.dto;

public class ProjectReportSummaryDTO {

    private long totalIssues;
    private long openIssues;
    private long inProgress;
    private long testing;
    private long completed;
    private double completionRate;
    private long highPriority;
    private long mediumPriority;
    private long lowPriority;

    public ProjectReportSummaryDTO() {
    }

    public long getTotalIssues() {
        return totalIssues;
    }

    public void setTotalIssues(long totalIssues) {
        this.totalIssues = totalIssues;
    }

    public long getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(long openIssues) {
        this.openIssues = openIssues;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getTesting() {
        return testing;
    }

    public void setTesting(long testing) {
        this.testing = testing;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public long getHighPriority() {
        return highPriority;
    }

    public void setHighPriority(long highPriority) {
        this.highPriority = highPriority;
    }

    public long getMediumPriority() {
        return mediumPriority;
    }

    public void setMediumPriority(long mediumPriority) {
        this.mediumPriority = mediumPriority;
    }

    public long getLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(long lowPriority) {
        this.lowPriority = lowPriority;
    }
}
