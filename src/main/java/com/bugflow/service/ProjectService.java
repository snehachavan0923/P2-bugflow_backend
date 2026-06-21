package com.bugflow.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bugflow.entity.Project;
import com.bugflow.entity.ProjectMember;
import com.bugflow.entity.User;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.exception.UnauthorizedException;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.UserRepository;

@Service
public class ProjectService {

    @Autowired 
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private UserRepository userRepository;

    public Project createProject(Project project) {

    	User owner =
    	        userRepository.findById(
    	                project.getOwnerId())
    	        .orElseThrow(() ->
    	                new ResourceNotFoundException(
    	                        "Owner not found"));

        if(owner.getOrganizationId() == null) {

            throw new BadRequestException(
                    "Please create an organization first");
        }

        if(project.getName() == null ||
           project.getName().isBlank()) {

            throw new BadRequestException(
                    "Project name is required");
        }

        if(project.getDescription() == null ||
           project.getDescription().isBlank()) {

            throw new BadRequestException(
                    "Project description is required");
        }

        project.setOrganizationId(
                owner.getOrganizationId());

        return projectRepository.save(project);
    }

    public List<Project> getProjectsForUser(
            String email) {

        User user =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        /*
         * OWNER
         */
        if(user.getRole().name().equals("Owner")) {

            return projectRepository.findByOrganizationId(
                    user.getOrganizationId());
        }

        /*
         * Developer / Tester / Viewer
         */
        List<ProjectMember> memberships =
                projectMemberRepository.findByEmail(
                        email);

        List<Project> projects =
                new ArrayList<>();

        for(ProjectMember member : memberships) {

            projectRepository.findById(
                    member.getProjectId())
                    .ifPresent(projects::add);
        }

        return projects;
    }

    public Project getProjectForUser(
            String projectId,
            String email) {

        Project project =
                projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        User user =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        /*
         * Organization Security
         */
        if(user.getOrganizationId() == null ||
           !user.getOrganizationId()
                .equals(project.getOrganizationId())) {

            throw new UnauthorizedException(
                    "Project belongs to another organization");
        }

        /*
         * Owner Access
         */
        if(user.getId()
                .equals(project.getOwnerId())) {

            return project;
        }
        /*
         * Project Member Access
         */
        boolean memberExists =
                projectMemberRepository
                        .existsByProjectIdAndEmail(
                                projectId,
                                email);

        if(memberExists) {
            return project;
        }

        throw new UnauthorizedException(
                "You are not allowed to access this project");
    }
}