package com.bugflow.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bugflow.dto.AcceptInvitationRequest;
import com.bugflow.dto.InvitationInfoResponse;
import com.bugflow.entity.Invitation;
import com.bugflow.entity.ProjectMember;
import com.bugflow.repository.InvitationRepository;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.service.InvitationService;

@RestController
@RequestMapping("/invitations")
@CrossOrigin(origins = "*")
public class InvitationController {

    private final InvitationService invitationService;
    private final InvitationRepository invitationRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectRepository projectRepository;

    public InvitationController(InvitationService invitationService,
                                InvitationRepository invitationRepository,
                                OrganizationRepository organizationRepository,
                                ProjectRepository projectRepository) {
        this.invitationService = invitationService;
        this.invitationRepository = invitationRepository;
        this.organizationRepository = organizationRepository;
        this.projectRepository = projectRepository;
    }

    @PostMapping("/accept")
    public ResponseEntity<ProjectMember> acceptInvitation(@RequestBody AcceptInvitationRequest request) {

        ProjectMember member = invitationService.acceptInvitation(request);

        return ResponseEntity.ok(member);
    }

    @GetMapping("/info")
    public ResponseEntity<InvitationInfoResponse> getInvitationInfo(@RequestParam String token) {

        Optional<Invitation> invOpt = invitationRepository.findByToken(token);

        if (invOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Invitation inv = invOpt.get();

        InvitationInfoResponse resp = new InvitationInfoResponse();
        resp.setOrganizationId(inv.getOrganizationId());

        var orgOpt = organizationRepository.findById(inv.getOrganizationId());
        resp.setOrganizationName(orgOpt.map(o -> o.getName()).orElse("Organization"));

        resp.setProjectId(inv.getProjectId());
        var projOpt = projectRepository.findById(inv.getProjectId());
        resp.setProjectName(projOpt.map(p -> p.getName()).orElse("Project"));

        resp.setEmail(inv.getEmail());
        resp.setRole(inv.getRole());
        resp.setExpiresAt(inv.getExpiresAt());
        resp.setStatus(inv.getStatus());

        return ResponseEntity.ok(resp);
    }
}
