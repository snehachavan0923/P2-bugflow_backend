package com.bugflow.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bugflow.dto.AddMemberRequest;
import com.bugflow.entity.ProjectMember;
import com.bugflow.entity.Role;
import com.bugflow.entity.User;
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

        projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));

        if (projectMemberRepository.existsByProjectIdAndEmail(
                projectId,
                request.getEmail())) {

            throw new RuntimeException(
                    "Member already exists in this project");
        }

        User user =
                userRepository.findByEmail(
                        request.getEmail())
                        .orElse(null);

        if (user == null) {

            user = new User();

            user.setName(request.getName());
            user.setEmail(request.getEmail());

            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()));

            user.setRole(request.getRole());

            user = userRepository.save(user);
        }

        ProjectMember member = new ProjectMember();

        member.setProjectId(projectId);
        member.setUserId(user.getId());

        member.setName(user.getName());
        member.setEmail(user.getEmail());

        member.setRole(request.getRole());

        return projectMemberRepository.save(member);
    }

    public List<ProjectMember> getMembers(
            String projectId) {

        projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));

        return projectMemberRepository.findByProjectId(projectId);
    }

    public ProjectMember updateRole(
            String memberId,
            Role role) {

        ProjectMember member =
                projectMemberRepository.findById(memberId)
                        .orElseThrow(() ->
                                new RuntimeException(
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
                                new RuntimeException(
                                        "Member not found"));

        if (!member.getProjectId().equals(projectId)) {

            throw new RuntimeException(
                    "Member does not belong to this project");
        }

        projectMemberRepository.delete(member);
    }
}