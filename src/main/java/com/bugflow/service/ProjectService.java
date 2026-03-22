package com.bugflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bugflow.entity.Project;
import com.bugflow.repository.ProjectRepository;



@Service

public class ProjectService {

    private  ProjectRepository projectRepository;

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(String id) {
        return projectRepository.findById(id).orElseThrow();
    }

    public void deleteProject(String id) {
        projectRepository.deleteById(id);
    }
}