package com.bugflow.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bugflow.entity.Project;
import com.bugflow.entity.ProjectMember;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.exception.UnauthorizedException;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    public Project createProject(Project project) {

        if (project.getName() == null ||
            project.getName().isBlank()) {

            throw new BadRequestException(
                    "Project name is required");
        }

        if (project.getDescription() == null ||
            project.getDescription().isBlank()) {

            throw new BadRequestException(
                    "Project description is required");
        }

        return projectRepository.save(project);
    }

    public List<Project> getProjectsForUser(String email) {

        List<Project> ownedProjects =
                projectRepository.findByOwnerId(email);

        List<ProjectMember> memberships =
                projectMemberRepository.findByEmail(email);

        Set<String> memberProjectIds =
                memberships.stream()
                        .map(ProjectMember::getProjectId)
                        .collect(Collectors.toSet());

        List<Project> memberProjects =
                projectRepository.findAllById(memberProjectIds);

        Map<String, Project> uniqueProjects =
                new LinkedHashMap<>();

        for (Project p : ownedProjects) {
            uniqueProjects.put(p.getId(), p);
        }

        for (Project p : memberProjects) {
            uniqueProjects.put(p.getId(), p);
        }

        return new ArrayList<>(uniqueProjects.values());
    }

    public Project getProjectForUser(
            String projectId,
            String email) {

        Project project =
                projectRepository.findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"));

        /*
         * Owner access
         */
        if (email.equals(project.getOwnerId())) {
            return project;
        }

        /*
         * Team member access
         */
        boolean memberExists =
                projectMemberRepository
                        .existsByProjectIdAndEmail(
                                projectId,
                                email);

        if (memberExists) {
            return project;
        }

        throw new UnauthorizedException(
                "You are not allowed to access this project");
    }
}