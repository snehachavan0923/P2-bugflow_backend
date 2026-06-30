package com.bugflow.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bugflow.dto.AddMemberRequest;
import com.bugflow.entity.Project;
import com.bugflow.entity.ProjectMember;
import com.bugflow.entity.Role;
import com.bugflow.entity.User;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.exception.UnauthorizedException;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.UserRepository;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProjectMemberService(
            ProjectMemberRepository projectMemberRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.projectMemberRepository = projectMemberRepository; 
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ProjectMember addMember(
            String projectId,
            AddMemberRequest request) {

        if (projectMemberRepository.existsByProjectIdAndEmail(
                projectId,
                request.getEmail())) {

            throw new BadRequestException(
                    "Member already exists in this project");
        }

        User user =
                userRepository.findByEmail(
                        request.getEmail())
                        .orElse(null);

        Project project =
                projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        if(user != null) {

            if(user.getOrganizationId() != null &&
               !user.getOrganizationId()
                    .equals(project.getOrganizationId())) {

                throw new BadRequestException(
                        "User belongs to another organization");
            }

            if(user.getOrganizationId() == null) {

                user.setOrganizationId(
                        project.getOrganizationId());

                userRepository.save(user);
            }
        }
        
        if(user != null &&
        		   user.getRole() == Role.Owner) {

        		    throw new BadRequestException(
        		            "Owners cannot be added as project members");
        		}

        if (user == null) {

            user = new User();

            user.setName(request.getName());
            user.setEmail(request.getEmail());

            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()));

            user.setRole(request.getRole());

            user.setOrganizationId(
                    project.getOrganizationId());

            user = userRepository.save(user);
        }

        ProjectMember member = new ProjectMember();

        member.setProjectId(projectId);
        member.setUserId(user.getId());

        member.setName(user.getName());
        member.setEmail(user.getEmail());

        member.setRole(request.getRole());
        member.setOrganizationId(
                project.getOrganizationId());

        return projectMemberRepository.save(member);
    }

    public List<ProjectMember> getMembers(
            String projectId) {

        projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found"));

        return projectMemberRepository.findByProjectId(projectId);
    }

    public ProjectMember updateRole(
            String memberId,
            Role role) {

        ProjectMember member =
                projectMemberRepository.findById(memberId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Member not found"));

        member.setRole(role);

        return projectMemberRepository.save(member);
    }

    public void removeMember(
            String projectId,
            String memberId) {

        ProjectMember member =
                projectMemberRepository.findById(memberId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Member not found"));

        if (!member.getProjectId().equals(projectId)) {

            throw new UnauthorizedException(
                    "Member does not belong to this project");
        }

        User user =
                userRepository.findById(
                        member.getUserId())
                .orElse(null);

        projectMemberRepository.delete(member);

        if(user != null &&
           !projectMemberRepository.existsByUserId(
                   user.getId())) {

            user.setOrganizationId(null);

            userRepository.save(user);
        }
    }
    
    public void changeMemberPassword(
            String projectId,
            String memberId,
            String newPassword) {

        ProjectMember member =
                projectMemberRepository.findById(memberId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Member not found"));

        if (!member.getProjectId().equals(projectId)) {
            throw new UnauthorizedException(
                    "Member does not belong to this project");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new BadRequestException(
                    "Password is required");
        }

        if (!newPassword.matches(
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {

            throw new BadRequestException(
                    "Password must be at least 8 characters and contain an uppercase letter, lowercase letter, number, and special character.");
        }

        User user =
                userRepository.findById(member.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"));
        
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException(
                    "Please enter a new password. The new password cannot be the same as the current password.");
        }


        user.setPassword(
                passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }
}