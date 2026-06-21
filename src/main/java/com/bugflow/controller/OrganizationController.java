package com.bugflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bugflow.dto.OrganizationMemberResponse;
import com.bugflow.dto.OrganizationRequest;
import com.bugflow.dto.OrganizationSettingsResponse;
import com.bugflow.entity.Organization;
import com.bugflow.service.OrganizationService;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    private final OrganizationService
            organizationService;

    public OrganizationController(
            OrganizationService organizationService) {

        this.organizationService =
                organizationService;
    }

    @PostMapping
    public ResponseEntity<Organization>
    createOrganization(
            @RequestBody
            OrganizationRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                organizationService
                        .createOrganization(
                                authentication.getName(),
                                request));
    }
    
    @GetMapping("/me")
    public ResponseEntity<Organization>
    getMyOrganization(
            Authentication authentication) {

        return ResponseEntity.ok(
                organizationService
                        .getMyOrganization(
                                authentication.getName()));
    }
    
    @GetMapping("/members")
    public ResponseEntity<
            List<OrganizationMemberResponse>>
    getOrganizationMembers(
            Authentication authentication) {

        return ResponseEntity.ok(
                organizationService
                        .getOrganizationMembers(
                                authentication.getName()));
    }
    
    @GetMapping("/settings")
    public ResponseEntity<
            OrganizationSettingsResponse>
    getOrganizationSettings(
            Authentication authentication) {

        return ResponseEntity.ok(
                organizationService
                        .getOrganizationSettings(
                                authentication.getName()));
    }
}