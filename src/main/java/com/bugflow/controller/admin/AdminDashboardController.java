package com.bugflow.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugflow.dto.admin.AdminDashboardStatsDTO;
import com.bugflow.service.admin.AdminDashboardService;

@RestController
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('Admin')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    /**
     * Get admin dashboard statistics and recent activities.
     * 
     * Returns:
     * - Total Organizations
     * - Total Users
     * - Total Projects
     * - Total Revenue
     * - Active Subscriptions
     * - Expired Subscriptions
     * - Recent Activities (top 10)
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardStatsDTO> getDashboardStats() {
        AdminDashboardStatsDTO stats = adminDashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}
