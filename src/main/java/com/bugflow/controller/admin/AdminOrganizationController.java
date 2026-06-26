package com.bugflow.controller.admin;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bugflow.dto.admin.AdminOrganizationResponseDTO;
import com.bugflow.service.admin.AdminOrganizationService;

@RestController
@RequestMapping("/admin/organizations")
public class AdminOrganizationController {

    private final AdminOrganizationService service;

    public AdminOrganizationController(
            AdminOrganizationService service) {

        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AdminOrganizationResponseDTO>>
    getOrganizations() {

        return ResponseEntity.ok(
                service.getOrganizations());
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<AdminOrganizationResponseDTO>
    getOrganization(
            @PathVariable String organizationId) {

        return ResponseEntity.ok(
                service.getOrganization(organizationId));
    }

    @PutMapping("/{organizationId}/suspend")
    public ResponseEntity<String> suspendOrganization(
            @PathVariable String organizationId) {

        service.suspendOrganization(organizationId);

        return ResponseEntity.ok(
                "Organization suspended successfully");
    }

    @PutMapping("/{organizationId}/activate")
    public ResponseEntity<String> activateOrganization(
            @PathVariable String organizationId) {

        service.activateOrganization(organizationId);

        return ResponseEntity.ok(
                "Organization activated successfully");
    }
}