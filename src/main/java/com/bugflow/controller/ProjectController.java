package com.bugflow.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bugflow.entity.Project;
import com.bugflow.entity.User;
import com.bugflow.repository.UserRepository;
import com.bugflow.service.ProjectService;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Project> createProject(
            @RequestBody Project project,
            Authentication authentication) {

    	String email = authentication.getName();

    	User owner =
    	        userRepository.findByEmail(email)
    	        .orElseThrow();

    	project.setOwnerId(
    	        owner.getId());

    	project.setOrganizationId(
    	        owner.getOrganizationId());

        return ResponseEntity.ok(
                projectService.createProject(project));
    }

    @GetMapping
    public ResponseEntity<List<Project>> getProjects(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                projectService.getProjectsForUser(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProject(
            @PathVariable String id,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                projectService.getProjectForUser(
                        id,
                        email));
    }
}