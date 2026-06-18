package com.bugflow.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bugflow.entity.Project;
import com.bugflow.service.ProjectService;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<Project> createProject(
            @RequestBody Project project,
            Authentication authentication) {

        String email = authentication.getName();

        project.setOwnerId(email);

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