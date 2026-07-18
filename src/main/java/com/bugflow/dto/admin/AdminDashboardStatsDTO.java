package com.bugflow.dto.admin;

import java.math.BigDecimal;
import java.util.List;
import com.bugflow.dto.ActivityDTO;

public class AdminDashboardStatsDTO {
    
    private long totalOrganizations;
    private long totalUsers;
    private long totalProjects;
    private BigDecimal totalRevenue;
    private long activeSubscriptions;
    private long expiredSubscriptions;
    private List<ActivityDTO> recentActivities;

    public AdminDashboardStatsDTO() {
    }

    public AdminDashboardStatsDTO(long totalOrganizations, long totalUsers, long totalProjects,
                                 BigDecimal totalRevenue, long activeSubscriptions,
                                 long expiredSubscriptions, List<ActivityDTO> recentActivities) {
        this.totalOrganizations = totalOrganizations;
        this.totalUsers = totalUsers;
        this.totalProjects = totalProjects;
        this.totalRevenue = totalRevenue;
        this.activeSubscriptions = activeSubscriptions;
        this.expiredSubscriptions = expiredSubscriptions;
        this.recentActivities = recentActivities;
    }

    // Getters and Setters
    public long getTotalOrganizations() {
        return totalOrganizations;
    }

    public void setTotalOrganizations(long totalOrganizations) {
        this.totalOrganizations = totalOrganizations;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getActiveSubscriptions() {
        return activeSubscriptions;
    }

    public void setActiveSubscriptions(long activeSubscriptions) {
        this.activeSubscriptions = activeSubscriptions;
    }

    public long getExpiredSubscriptions() {
        return expiredSubscriptions;
    }

    public void setExpiredSubscriptions(long expiredSubscriptions) {
        this.expiredSubscriptions = expiredSubscriptions;
    }

    public List<ActivityDTO> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<ActivityDTO> recentActivities) {
        this.recentActivities = recentActivities;
    }
}
