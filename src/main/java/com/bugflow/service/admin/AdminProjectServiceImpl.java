package com.bugflow.service.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bugflow.dto.admin.AdminProjectResponseDTO;
import com.bugflow.entity.Organization;
import com.bugflow.entity.Project;
import com.bugflow.entity.User;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.IssueRepository;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.UserRepository;

@Service
public class AdminProjectServiceImpl
        implements AdminProjectService {

    private final ProjectRepository projectRepository;

    private final OrganizationRepository organizationRepository;

    private final UserRepository userRepository;

    private final IssueRepository issueRepository;

    private final ProjectMemberRepository projectMemberRepository;

    public AdminProjectServiceImpl(
            ProjectRepository projectRepository,
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            IssueRepository issueRepository,
            ProjectMemberRepository projectMemberRepository) {

        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    public List<AdminProjectResponseDTO> getProjects() {

        return projectRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapProject)
                .collect(Collectors.toList());
    }

    @Override
    public AdminProjectResponseDTO getProject(
            String projectId) {

        Project project =
                projectRepository.findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"));

        return mapProject(project);
    }

    private AdminProjectResponseDTO mapProject(
            Project project) {

        AdminProjectResponseDTO dto =
                new AdminProjectResponseDTO();

        dto.setProjectId(
                project.getId());

        dto.setProjectName(
                project.getName());

        Organization organization =
                organizationRepository
                        .findById(project.getOrganizationId())
                        .orElse(null);

        dto.setOrganizationName(
                organization != null
                        ? organization.getName()
                        : "Unknown");

        User owner =
                userRepository
                        .findById(project.getOwnerId())
                        .orElse(null);

        dto.setOwnerName(
                owner != null
                        ? owner.getName()
                        : "Unknown");

        /*
         * Phase 1
         * All projects are considered ACTIVE.
         * Project status can be introduced later.
         */
        dto.setStatus("ACTIVE");

        dto.setTotalIssues(
                issueRepository.countByProjectId(
                        project.getId()));

        dto.setTotalMembers(
                projectMemberRepository.countByProjectId(
                        project.getId()));

        dto.setCreatedAt(
                project.getCreatedAt());

        return dto;
    }
}