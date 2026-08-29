package com.bugflow.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.bugflow.dto.AcceptInvitationRequest;
import com.bugflow.dto.AddMemberRequest;
import com.bugflow.dto.CreateInvitationRequest;
import com.bugflow.entity.Invitation;
import com.bugflow.entity.InvitationStatus;
import com.bugflow.entity.Project;
import com.bugflow.entity.ProjectMember;
import com.bugflow.entity.Role;
import com.bugflow.entity.User;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.InvitationRepository;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.UserRepository;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final MailService mailService;
    private final ProjectMemberService projectMemberService;

    public InvitationService(
            InvitationRepository invitationRepository,
            ProjectRepository projectRepository,
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository,
            MailService mailService,
            ProjectMemberService projectMemberService) {

        this.invitationRepository = invitationRepository;
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.mailService = mailService;
        this.projectMemberService = projectMemberService;
    }

    public ProjectMember acceptInvitation(AcceptInvitationRequest request) {

        if (request == null || request.getToken() == null || request.getToken().isBlank()) {
            throw new BadRequestException("Token is required");
        }

        var invOpt = invitationRepository.findByToken(request.getToken());
        if (invOpt.isEmpty()) {
            throw new ResourceNotFoundException("Invitation not found");
        }

        Invitation inv = invOpt.get();

        if (inv.getStatus() != InvitationStatus.PENDING) {
            throw new BadRequestException("Invitation is not pending");
        }

        if (inv.getExpiresAt() != null && inv.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new BadRequestException("Invitation has expired");
        }

        // Build add member request using token-sourced details
        AddMemberRequest addReq = new AddMemberRequest();
        addReq.setName(request.getName());
        addReq.setEmail(inv.getEmail());
        addReq.setPassword(request.getPassword());
        addReq.setRole(inv.getRole());

        // Use ProjectMemberService to perform creation/validation
        ProjectMember member = projectMemberService.addMember(inv.getProjectId(), addReq);

        // mark invitation accepted and invalidate token
        inv.setStatus(InvitationStatus.ACCEPTED);
        inv.setAcceptedAt(java.time.LocalDateTime.now());
        inv.setToken(null);
        invitationRepository.save(inv);

        return member;
    }

    public Invitation createInvitation(String projectId, CreateInvitationRequest request, String inviterEmail) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();

        Role inviteRole;
        try {
            inviteRole = Role.valueOf(request.getRole());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid role");
        }

        if (inviteRole == Role.Owner || inviteRole == Role.Admin) {
            throw new BadRequestException("Cannot invite Owner or Admin roles");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        String orgId = project.getOrganizationId();
        if (orgId == null) {
            throw new BadRequestException("Project has no organization");
        }

        // Validate inviter is owner of organization
        User inviter = userRepository.findByEmail(inviterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Inviter user not found"));

        if (inviter.getOrganizationId() == null || !Objects.equals(inviter.getOrganizationId(), orgId)) {
            throw new BadRequestException("Inviter does not belong to this organization");
        }

        // Check inviter role is Owner and is the organization owner
        if (inviter.getRole() != Role.Owner) {
            throw new BadRequestException("Only organization owners can invite members");
        }

        // Organization ownership
        var orgOpt = organizationRepository.findById(orgId);
        if (orgOpt.isEmpty()) {
            throw new ResourceNotFoundException("Organization not found");
        }
        if (!Objects.equals(orgOpt.get().getOwnerId(), inviter.getId())) {
            throw new BadRequestException("Only the organization owner can invite members");
        }

        // Check project membership
        if (projectMemberRepository.existsByProjectIdAndEmail(projectId, normalizedEmail)) {
            throw new BadRequestException("User is already a member of this project");
        }

        // Check existing invitations
        if (invitationRepository.existsByEmailAndProjectIdAndStatus(normalizedEmail, projectId, InvitationStatus.PENDING)) {
            throw new BadRequestException("An active invitation already exists for this email and project");
        }

        // Check if user exists and is member of another org
        var existingUserOpt = userRepository.findByEmail(normalizedEmail);
        if (existingUserOpt.isPresent()) {
            User existing = existingUserOpt.get();
            if (existing.getOrganizationId() != null && !Objects.equals(existing.getOrganizationId(), orgId)) {
                throw new BadRequestException("User belongs to another organization");
            }
        }

        Invitation inv = new Invitation(orgId, projectId, normalizedEmail, inviteRole, inviter.getId());
        inv.setCreatedAt(LocalDateTime.now());
        inv.setStatus(InvitationStatus.PENDING);

        Invitation saved = invitationRepository.save(inv);

        try {
            mailService.sendInvitationEmail(saved);
        } catch (Exception ex) {
            // Mark invitation as revoked so it's not considered active
            saved.setStatus(com.bugflow.entity.InvitationStatus.REVOKED);
            invitationRepository.save(saved);
            throw new BadRequestException("Failed to send invitation email: " + ex.getMessage());
        }

        return saved;
    }
}
