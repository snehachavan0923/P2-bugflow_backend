package com.bugflow.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bugflow.dto.ProjectInsightsDTO;
import com.bugflow.dto.ProjectReportSummaryDTO;
import com.bugflow.dto.ProjectTrendsReportDTO;
import com.bugflow.dto.TeamPerformanceReportDTO;
import com.bugflow.dto.WeeklyDataDTO;
import com.bugflow.entity.Issue;
import com.bugflow.entity.Project;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.IssueRepository;
import com.bugflow.repository.ProjectRepository;

@Service
public class ReportsService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IssueRepository issueRepository;

    public ProjectReportSummaryDTO getProjectSummary(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        List<Issue> issues = issueRepository.findByProjectId(project.getId());

        ProjectReportSummaryDTO summary = new ProjectReportSummaryDTO();

        long totalIssues = issues.size();
        long openIssues = 0;
        long inProgress = 0;
        long testing = 0;
        long completed = 0;
        long highPriority = 0;
        long mediumPriority = 0;
        long lowPriority = 0;

        for (Issue issue : issues) {
            String status = issue.getStatus();
            if (status != null) {
                switch (status) {
                    case "Open" -> openIssues++;
                    case "In Progress" -> inProgress++;
                    case "Review" -> testing++;
                    case "Done" -> completed++;
                    default -> {
                    }
                }
            }

            String priority = issue.getPriority();
            if (priority != null) {
                switch (priority) {
                    case "High" -> highPriority++;
                    case "Medium" -> mediumPriority++;
                    case "Low" -> lowPriority++;
                    default -> {
                    }
                }
            }
        }

        double completionRate = totalIssues == 0 ? 0
                : ((double) completed / (double) totalIssues) * 100.0;

        summary.setTotalIssues(totalIssues);
        summary.setOpenIssues(openIssues);
        summary.setInProgress(inProgress);
        summary.setTesting(testing);
        summary.setCompleted(completed);
        summary.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);
        summary.setHighPriority(highPriority);
        summary.setMediumPriority(mediumPriority);
        summary.setLowPriority(lowPriority);

        return summary;
    }

    public List<TeamPerformanceReportDTO> getTeamPerformance(String projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        List<Issue> issues = issueRepository.findByProjectId(project.getId());

        Map<String, TeamPerformanceReportDTO> performanceMap = new HashMap<>();

        for (Issue issue : issues) {

            // =========================
            // DEVELOPER
            // =========================

            if (issue.getDeveloperName() != null &&
                    !issue.getDeveloperName().isBlank()) {

                TeamPerformanceReportDTO developer =
                        performanceMap.computeIfAbsent(
                                issue.getDeveloperName(),
                                name -> {
                                    TeamPerformanceReportDTO dto =
                                            new TeamPerformanceReportDTO();
                                    dto.setName(name);
                                    return dto;
                                });

                // Every issue assigned to this developer
                developer.setAssigned(
                        developer.getAssigned() + 1);

                // Development completed
                if ("Review".equalsIgnoreCase(issue.getStatus())
                        || "Done".equalsIgnoreCase(issue.getStatus())) {

                    developer.setCompleted(
                            developer.getCompleted() + 1);
                }
            }

            // =========================
            // TESTER
            // =========================

            if (issue.getTesterName() != null &&
                    !issue.getTesterName().isBlank()) {

                TeamPerformanceReportDTO tester =
                        performanceMap.computeIfAbsent(
                                issue.getTesterName(),
                                name -> {
                                    TeamPerformanceReportDTO dto =
                                            new TeamPerformanceReportDTO();
                                    dto.setName(name);
                                    return dto;
                                });

                // Every issue assigned to this tester
                tester.setAssigned(
                        tester.getAssigned() + 1);

                // Testing completed
                if ("Done".equalsIgnoreCase(issue.getStatus())) {

                    tester.setCompleted(
                            tester.getCompleted() + 1);
                }
            }
        }

        for (TeamPerformanceReportDTO report : performanceMap.values()) {

            report.setPending(
                    report.getAssigned() - report.getCompleted());

            report.setCompletionRate(
                    report.getAssigned() == 0
                            ? 0
                            : (int)Math.round(
                                    (double) report.getCompleted()
                                    / report.getAssigned()
                                    * 100));
        }

        return new ArrayList<>(performanceMap.values());
    }

    public ProjectTrendsReportDTO getProjectTrends(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        List<Issue> issues = issueRepository.findByProjectId(project.getId());

        // Calculate weekly created and resolved issues
        Map<String, Integer> weeklyCreatedMap = new HashMap<>();
        Map<String, Integer> weeklyResolvedMap = new HashMap<>();
        List<Double> resolutionTimes = new ArrayList<>();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (Issue issue : issues) {
            // Count weekly created
            if (issue.getCreatedAt() != null) {
                int week = issue.getCreatedAt().get(weekFields.weekOfYear());
                String weekKey = "Week " + week;
                weeklyCreatedMap.put(weekKey, weeklyCreatedMap.getOrDefault(weekKey, 0) + 1);
            }

            // Count weekly resolved and calculate resolution time
            if ("Done".equalsIgnoreCase(issue.getStatus()) && issue.getUpdatedAt() != null
                    && issue.getCreatedAt() != null) {
                int week = issue.getUpdatedAt().get(weekFields.weekOfYear());
                String weekKey = "Week " + week;
                weeklyResolvedMap.put(weekKey, weeklyResolvedMap.getOrDefault(weekKey, 0) + 1);

                // Calculate resolution time in days
                long millis = java.time.temporal.ChronoUnit.MILLIS.between(
                        issue.getCreatedAt(),
                        issue.getUpdatedAt());
                double days = millis / (1000.0 * 60.0 * 60.0 * 24.0);
                resolutionTimes.add(days);
            }
        }

        // Convert maps to sorted lists
        List<WeeklyDataDTO> weeklyCreated = new ArrayList<>();
        List<WeeklyDataDTO> weeklyResolved = new ArrayList<>();

        for (int i = 1; i <= 52; i++) {
            String weekKey = "Week " + i;
            weeklyCreated.add(new WeeklyDataDTO(weekKey, weeklyCreatedMap.getOrDefault(weekKey, 0)));
            weeklyResolved.add(new WeeklyDataDTO(weekKey, weeklyResolvedMap.getOrDefault(weekKey, 0)));
        }

        // Calculate resolution statistics
        double averageResolutionDays = 0;
        double fastestResolutionDays = 0;
        double slowestResolutionDays = 0;

        if (!resolutionTimes.isEmpty()) {
            averageResolutionDays = resolutionTimes.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0);
            fastestResolutionDays = resolutionTimes.stream()
                    .mapToDouble(Double::doubleValue)
                    .min()
                    .orElse(0);
            slowestResolutionDays = resolutionTimes.stream()
                    .mapToDouble(Double::doubleValue)
                    .max()
                    .orElse(0);

            // Round to 1 decimal place
            averageResolutionDays = Math.round(averageResolutionDays * 10.0) / 10.0;
            fastestResolutionDays = Math.round(fastestResolutionDays * 10.0) / 10.0;
            slowestResolutionDays = Math.round(slowestResolutionDays * 10.0) / 10.0;
        }

        return new ProjectTrendsReportDTO(
                weeklyCreated,
                weeklyResolved,
                averageResolutionDays,
                fastestResolutionDays,
                slowestResolutionDays);
    }

    public ProjectInsightsDTO getProjectInsights(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        List<Issue> issues = issueRepository.findByProjectId(project.getId());

        // Get oldest open issue
        String oldestOpenIssue = issues.stream()
                .filter(issue -> !"Done".equalsIgnoreCase(issue.getStatus()))
                .min((a, b) -> a.getCreatedAt() != null && b.getCreatedAt() != null
                        ? a.getCreatedAt().compareTo(b.getCreatedAt())
                        : 0)
                .map(Issue::getTitle)
                .orElse("N/A");

        // Count highest priority open issues
        int highestPriorityOpen = (int) issues.stream()
                .filter(issue -> "High".equalsIgnoreCase(issue.getPriority())
                        && !"Done".equalsIgnoreCase(issue.getStatus()))
                .count();

        // Count overdue issues (created > 7 days ago and not done)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        int overdueIssues = (int) issues.stream()
                .filter(issue -> issue.getCreatedAt() != null
                        && issue.getCreatedAt().isBefore(sevenDaysAgo)
                        && !"Done".equalsIgnoreCase(issue.getStatus()))
                .count();

        // Calculate developer activity (most and least active)
        // Calculate developer activity (completed development work)
        Map<String, Integer> developerActivity = new HashMap<>();

        // Add every developer with 0 completed tasks
        for (Issue issue : issues) {

            if (issue.getDeveloperName() != null
                    && !issue.getDeveloperName().isBlank()) {

                developerActivity.putIfAbsent(
                        issue.getDeveloperName(),
                        0);
            }
        }

        // Count completed development work
        for (Issue issue : issues) {

            if (issue.getDeveloperName() == null
                    || issue.getDeveloperName().isBlank()) {
                continue;
            }

            // Developer work is complete once issue reaches Review or Done
            if ("Review".equalsIgnoreCase(issue.getStatus())
                    || "Done".equalsIgnoreCase(issue.getStatus())) {

                developerActivity.put(
                        issue.getDeveloperName(),
                        developerActivity.get(issue.getDeveloperName()) + 1);
            }
        }
        String mostActiveDeveloper = "N/A";
        String leastActiveDeveloper = "N/A";
        if (!developerActivity.isEmpty()) {
            mostActiveDeveloper = developerActivity.entrySet().stream()
                    .max((a, b) -> a.getValue().compareTo(b.getValue()))
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

            leastActiveDeveloper = developerActivity.entrySet().stream()
                    .min((a, b) -> a.getValue().compareTo(b.getValue()))
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
        }

        // Count resolved issues this month
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDayOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        int totalResolvedThisMonth = (int) issues.stream()
                .filter(issue -> "Done".equalsIgnoreCase(issue.getStatus())
                        && issue.getUpdatedAt() != null
                        && issue.getUpdatedAt().isAfter(firstDayOfMonth))
                .count();

        return new ProjectInsightsDTO(
                oldestOpenIssue,
                highestPriorityOpen,
                overdueIssues,
                mostActiveDeveloper,
                leastActiveDeveloper,
                totalResolvedThisMonth);
    }
}
