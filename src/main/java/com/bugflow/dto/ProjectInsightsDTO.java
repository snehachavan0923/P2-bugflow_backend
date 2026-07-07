package com.bugflow.dto;

public class ProjectInsightsDTO {
    private String oldestOpenIssue;
    private int highestPriorityOpen;
    private int overdueIssues;
    private String mostActiveDeveloper;
    private String leastActiveDeveloper;
    private int totalResolvedThisMonth;

    public ProjectInsightsDTO() {
    }

    public ProjectInsightsDTO(
            String oldestOpenIssue,
            int highestPriorityOpen,
            int overdueIssues,
            String mostActiveDeveloper,
            String leastActiveDeveloper,
            int totalResolvedThisMonth) {
        this.oldestOpenIssue = oldestOpenIssue;
        this.highestPriorityOpen = highestPriorityOpen;
        this.overdueIssues = overdueIssues;
        this.mostActiveDeveloper = mostActiveDeveloper;
        this.leastActiveDeveloper = leastActiveDeveloper;
        this.totalResolvedThisMonth = totalResolvedThisMonth;
    }

    public String getOldestOpenIssue() {
        return oldestOpenIssue;
    }

    public void setOldestOpenIssue(String oldestOpenIssue) {
        this.oldestOpenIssue = oldestOpenIssue;
    }

    public int getHighestPriorityOpen() {
        return highestPriorityOpen;
    }

    public void setHighestPriorityOpen(int highestPriorityOpen) {
        this.highestPriorityOpen = highestPriorityOpen;
    }

    public int getOverdueIssues() {
        return overdueIssues;
    }

    public void setOverdueIssues(int overdueIssues) {
        this.overdueIssues = overdueIssues;
    }

    public String getMostActiveDeveloper() {
        return mostActiveDeveloper;
    }

    public void setMostActiveDeveloper(String mostActiveDeveloper) {
        this.mostActiveDeveloper = mostActiveDeveloper;
    }

    public String getLeastActiveDeveloper() {
        return leastActiveDeveloper;
    }

    public void setLeastActiveDeveloper(String leastActiveDeveloper) {
        this.leastActiveDeveloper = leastActiveDeveloper;
    }

    public int getTotalResolvedThisMonth() {
        return totalResolvedThisMonth;
    }

    public void setTotalResolvedThisMonth(int totalResolvedThisMonth) {
        this.totalResolvedThisMonth = totalResolvedThisMonth;
    }
}
