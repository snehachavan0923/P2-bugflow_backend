package com.bugflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bugflow.dto.DashboardStatsResponse;
import com.bugflow.entity.Organization;
import com.bugflow.entity.Project;
import com.bugflow.entity.ProjectMember;
import com.bugflow.entity.User;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.IssueRepository;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.UserRepository;
import com.bugflow.dto.DeveloperDashboardResponse;
import com.bugflow.dto.TesterDashboardResponse;
@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;
    private final ProjectMemberRepository
    projectMemberRepository;
    private final OrganizationRepository
    organizationRepository;

    public DashboardService(
            UserRepository userRepository,
            ProjectRepository projectRepository,
            IssueRepository issueRepository,
            ProjectMemberRepository projectMemberRepository,
            OrganizationRepository organizationRepository) {

        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
        this.projectMemberRepository =
                projectMemberRepository;
        this.organizationRepository =
                organizationRepository;
    }

    public DashboardStatsResponse
    getOwnerDashboard(
            String email) {

        User owner =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        List<Project> projects =
                projectRepository.findByOrganizationId(
                        owner.getOrganizationId());

        List<String> projectIds =
                projects.stream()
                        .map(Project::getId)
                        .toList();

        DashboardStatsResponse dto =
                new DashboardStatsResponse();

        Organization organization =
                organizationRepository
                        .findById(
                                owner.getOrganizationId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization not found"));

        dto.setOrganizationName(
                organization.getName());

        dto.setTotalProjects(
                projects.size());

        long totalMembers =
                projects.stream()
                        .flatMap(project ->
                                projectMemberRepository
                                        .findByProjectId(
                                                project.getId())
                                        .stream())
                        .map(ProjectMember::getUserId)
                        .distinct()
                        .count();
        dto.setTotalMembers(
                totalMembers);

        dto.setTotalIssues(
                issueRepository
                        .countByProjectIdIn(
                                projectIds));

        dto.setAssignedTasks(
                issueRepository
                        .countByProjectIdInAndAssignedToUserIdIsNotNull(
                                projectIds));

        dto.setOpenIssues(
                issueRepository
                        .countByProjectIdInAndStatus(
                                projectIds,
                                "Open"));

        dto.setInProgressIssues(
                issueRepository
                        .countByProjectIdInAndStatus(
                                projectIds,
                                "In Progress"));

        dto.setReviewIssues(
                issueRepository
                        .countByProjectIdInAndStatus(
                                projectIds,
                                "Review"));

        dto.setResolvedIssues(
                issueRepository
                        .countByProjectIdInAndStatus(
                                projectIds,
                                "Done"));

        return dto;
    }
    
    public DeveloperDashboardResponse
    getDeveloperDashboard(
            String email) {

        DeveloperDashboardResponse dto =
                new DeveloperDashboardResponse();

        dto.setAssignedTasks(
                issueRepository
                        .countByAssignedToEmail(
                                email));

        dto.setInProgress(
                issueRepository
                        .countByAssignedToEmailAndStatus(
                                email,
                                "In Progress"));

        dto.setReview(
                issueRepository
                        .countByAssignedToEmailAndStatus(
                                email,
                                "Review"));

        return dto;
    }
    
    public TesterDashboardResponse
    getTesterDashboard(
            String email) {

        TesterDashboardResponse dto =
                new TesterDashboardResponse();

        dto.setCreatedIssues(
                issueRepository
                        .countByAssignedToRole(
                                "Tester"));

        dto.setPendingVerification(
                issueRepository
                        .countByAssignedToEmailAndStatus(
                                email,
                                "Review"));

        dto.setVerified(
                issueRepository
                        .countByAssignedToEmailAndStatus(
                                email,
                                "Done"));

        return dto;
    }
}