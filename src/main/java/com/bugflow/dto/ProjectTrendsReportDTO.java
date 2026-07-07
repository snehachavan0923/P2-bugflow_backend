package com.bugflow.dto;

import java.util.List;

public class ProjectTrendsReportDTO {
    private List<WeeklyDataDTO> weeklyCreated;
    private List<WeeklyDataDTO> weeklyResolved;
    private double averageResolutionDays;
    private double fastestResolutionDays;
    private double slowestResolutionDays;

    public ProjectTrendsReportDTO() {
    }

    public ProjectTrendsReportDTO(
            List<WeeklyDataDTO> weeklyCreated,
            List<WeeklyDataDTO> weeklyResolved,
            double averageResolutionDays,
            double fastestResolutionDays,
            double slowestResolutionDays) {
        this.weeklyCreated = weeklyCreated;
        this.weeklyResolved = weeklyResolved;
        this.averageResolutionDays = averageResolutionDays;
        this.fastestResolutionDays = fastestResolutionDays;
        this.slowestResolutionDays = slowestResolutionDays;
    }

    public List<WeeklyDataDTO> getWeeklyCreated() {
        return weeklyCreated;
    }

    public void setWeeklyCreated(List<WeeklyDataDTO> weeklyCreated) {
        this.weeklyCreated = weeklyCreated;
    }

    public List<WeeklyDataDTO> getWeeklyResolved() {
        return weeklyResolved;
    }

    public void setWeeklyResolved(List<WeeklyDataDTO> weeklyResolved) {
        this.weeklyResolved = weeklyResolved;
    }

    public double getAverageResolutionDays() {
        return averageResolutionDays;
    }

    public void setAverageResolutionDays(double averageResolutionDays) {
        this.averageResolutionDays = averageResolutionDays;
    }

    public double getFastestResolutionDays() {
        return fastestResolutionDays;
    }

    public void setFastestResolutionDays(double fastestResolutionDays) {
        this.fastestResolutionDays = fastestResolutionDays;
    }

    public double getSlowestResolutionDays() {
        return slowestResolutionDays;
    }

    public void setSlowestResolutionDays(double slowestResolutionDays) {
        this.slowestResolutionDays = slowestResolutionDays;
    }
}
