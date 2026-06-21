package com.bugflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import com.bugflow.dto.OrganizationMemberResponse;
import com.bugflow.dto.OrganizationRequest;
import com.bugflow.dto.OrganizationSettingsResponse;
import com.bugflow.entity.Organization;
import com.bugflow.entity.User;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.UserRepository;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    private final UserRepository userRepository;
    private final ProjectRepository
    projectRepository;

    public OrganizationService(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository) {

        this.organizationRepository =
                organizationRepository;

        this.userRepository =
                userRepository;

        this.projectRepository =
                projectRepository;
    }

    public Organization createOrganization(
            String email,
            OrganizationRequest request) {

        User owner =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found"));

        if(owner.getOrganizationId() != null) {

            throw new BadRequestException(
                    "Organization already exists");
        }

        Organization org =
                new Organization();

        org.setName(
                request.getName());

        org.setOwnerId(
                owner.getId());

        org =
                organizationRepository.save(org);

        owner.setOrganizationId(
                org.getId());

        userRepository.save(owner);

        return org;
    }
    
    public Organization getMyOrganization(
            String email) {

        User user =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        if(user.getOrganizationId() == null) {

            throw new ResourceNotFoundException(
                    "Organization not found");
        }

        return organizationRepository
                .findById(
                        user.getOrganizationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organization not found"));
    }
    
    public List<OrganizationMemberResponse>
    getOrganizationMembers(
            String email) {

        User owner =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        if(owner.getOrganizationId() == null) {

            throw new ResourceNotFoundException(
                    "Organization not found");
        }

        return userRepository
                .findByOrganizationId(
                        owner.getOrganizationId())
                .stream()
                .map(user -> {

                    OrganizationMemberResponse dto =
                            new OrganizationMemberResponse();

                    dto.setId(user.getId());

                    dto.setName(user.getName());

                    dto.setEmail(user.getEmail());

                    dto.setRole(user.getRole());

                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    public OrganizationSettingsResponse
    getOrganizationSettings(
            String email) {

        User user =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        if(user.getOrganizationId() == null) {

            throw new ResourceNotFoundException(
                    "Organization not found");
        }

        Organization organization =
                organizationRepository
                        .findById(
                                user.getOrganizationId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Organization not found"));

        OrganizationSettingsResponse dto =
                new OrganizationSettingsResponse();

        dto.setId(
                organization.getId());

        dto.setName(
                organization.getName());

        User owner =
                userRepository.findById(
                        organization.getOwnerId())
                .orElse(null);

        dto.setOwnerName(
                owner != null
                        ? owner.getName()
                        : "Unknown");

        dto.setCreatedAt(
                organization.getCreatedAt());

        dto.setTotalProjects(
                projectRepository
                        .countByOrganizationId(
                                organization.getId()));

        dto.setTotalMembers(
                userRepository
                        .countByOrganizationId(
                                organization.getId()));

        return dto;
    }
}