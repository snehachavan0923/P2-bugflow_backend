package com.bugflow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugflow.dto.ProjectInsightsDTO;
import com.bugflow.dto.ProjectReportSummaryDTO;
import com.bugflow.dto.ProjectTrendsReportDTO;
import com.bugflow.dto.TeamPerformanceReportDTO;
import com.bugflow.service.ReportsService;

@RestController
@RequestMapping("/projects/{projectId}/reports")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @GetMapping("/summary")
    public ResponseEntity<ProjectReportSummaryDTO> getProjectSummary(
            @PathVariable String projectId) {
        return ResponseEntity.ok(
                reportsService.getProjectSummary(projectId));
    }

    @GetMapping("/team")
    public ResponseEntity<List<TeamPerformanceReportDTO>> getTeamPerformance(
            @PathVariable String projectId) {
        return ResponseEntity.ok(
                reportsService.getTeamPerformance(projectId));
    }

    @GetMapping("/trends")
    public ResponseEntity<ProjectTrendsReportDTO> getProjectTrends(
            @PathVariable String projectId) {
        return ResponseEntity.ok(
                reportsService.getProjectTrends(projectId));
    }

    @GetMapping("/insights")
    public ResponseEntity<ProjectInsightsDTO> getProjectInsights(
            @PathVariable String projectId) {
        return ResponseEntity.ok(
                reportsService.getProjectInsights(projectId));
    }
}
