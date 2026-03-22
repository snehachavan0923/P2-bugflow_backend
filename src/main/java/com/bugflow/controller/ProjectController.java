package com.bugflow.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bugflow.entity.Project;
import com.bugflow.service.ProjectService;



@RestController
@RequestMapping("/api/projects")
@CrossOrigin("*")
public class ProjectController {

    private  ProjectService projectService;

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    @GetMapping
    public List<Project> getProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public Project getProject(@PathVariable String id) {
        return projectService.getProjectById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
    }
}