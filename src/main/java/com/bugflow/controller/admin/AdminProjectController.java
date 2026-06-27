package com.bugflow.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bugflow.dto.admin.AdminProjectResponseDTO;
import com.bugflow.service.admin.AdminProjectService;

@RestController
@RequestMapping("/admin/projects")
public class AdminProjectController {

    private final AdminProjectService adminProjectService;

    public AdminProjectController(
            AdminProjectService adminProjectService) {

        this.adminProjectService = adminProjectService;
    }

    @GetMapping
    public ResponseEntity<List<AdminProjectResponseDTO>>
    getProjects() {

        return ResponseEntity.ok(
                adminProjectService.getProjects());
    }
    @GetMapping("/{projectId}")
    public ResponseEntity<AdminProjectResponseDTO>
    getProject(
            @PathVariable String projectId) {

        return ResponseEntity.ok(
                adminProjectService.getProject(projectId));
    }
}