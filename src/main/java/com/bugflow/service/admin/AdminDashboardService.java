package com.bugflow.service.admin;

import com.bugflow.dto.admin.AdminDashboardStatsDTO;

public interface AdminDashboardService {
    
    /**
     * Get admin dashboard statistics including:
     * - Total Organizations
     * - Total Users
     * - Total Projects
     * - Total Revenue
     * - Active Subscriptions
     * - Expired Subscriptions
     * - Recent Activities (top 10)
     * 
     * @return AdminDashboardStatsDTO containing all dashboard metrics
     */
    AdminDashboardStatsDTO getDashboardStats();
}
