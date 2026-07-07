package com.bugflow.dto;

public class TeamPerformanceReportDTO {

    private String name;
    private long assigned;
    private long completed;
    private long pending;
    private int completionRate;

    public TeamPerformanceReportDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAssigned() {
        return assigned;
    }

    public void setAssigned(long assigned) {
        this.assigned = assigned;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getPending() {
        return pending;
    }

    public void setPending(long pending) {
        this.pending = pending;
    }

    public int getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(int completionRate) {
        this.completionRate = completionRate;
    }
}
