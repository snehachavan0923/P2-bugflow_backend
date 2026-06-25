package com.bugflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bugflow.dto.DashboardStatsResponse;
import com.bugflow.service.DashboardService;
import com.bugflow.service.IssueService;
import com.bugflow.dto.DeveloperDashboardResponse;
import com.bugflow.dto.TesterDashboardResponse;
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final IssueService issueService;

    public DashboardController(
            DashboardService dashboardService,
            IssueService issueService) {

        this.dashboardService = dashboardService;
        this.issueService = issueService;
    }

    @GetMapping("/owner")
    public ResponseEntity<DashboardStatsResponse>
    getOwnerDashboard(
            Authentication authentication) {

        return ResponseEntity.ok(
                dashboardService.getOwnerDashboard(
                        authentication.getName()));
    }

    @GetMapping("/issues")
    public ResponseEntity<?> getOrganizationIssues(
            Authentication authentication) {

        return ResponseEntity.ok(
                issueService.getOrganizationIssues(
                        authentication.getName()));
    }
    @GetMapping("/developer")
    public ResponseEntity<DeveloperDashboardResponse>
    getDeveloperDashboard(
            Authentication authentication) {

        return ResponseEntity.ok(
                dashboardService.getDeveloperDashboard(
                        authentication.getName()));
    }

    @GetMapping("/tester")
    public ResponseEntity<TesterDashboardResponse>
    getTesterDashboard(
            Authentication authentication) {

        return ResponseEntity.ok(
                dashboardService.getTesterDashboard(
                        authentication.getName()));
    }
    
    @GetMapping("/viewer")
    public ResponseEntity<DashboardStatsResponse>
    getViewerDashboard(Authentication authentication){

        return ResponseEntity.ok(
            dashboardService.getViewerDashboard(
                authentication.getName()
            )
        );
    }
}