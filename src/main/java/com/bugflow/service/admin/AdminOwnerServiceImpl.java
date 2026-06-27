package com.bugflow.service.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bugflow.dto.admin.AdminOwnerResponseDTO;
import com.bugflow.entity.Organization;
import com.bugflow.entity.OrganizationStatus;
import com.bugflow.entity.Role;
import com.bugflow.entity.User;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.UserRepository;

@Service
public class AdminOwnerServiceImpl
        implements AdminOwnerService {

    private final UserRepository userRepository;

    private final OrganizationRepository organizationRepository;

    public AdminOwnerServiceImpl(
            UserRepository userRepository,
            OrganizationRepository organizationRepository) {

        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public List<AdminOwnerResponseDTO> getOwners() {

        List<User> owners =
                userRepository.findByRole(Role.Owner);

        List<AdminOwnerResponseDTO> response =
                new ArrayList<>();

        for (User owner : owners) {

            Organization organization = null;

            if (owner.getOrganizationId() != null) {

                organization =
                        organizationRepository
                                .findById(owner.getOrganizationId())
                                .orElse(null);
            }

            AdminOwnerResponseDTO dto =
                    new AdminOwnerResponseDTO();

            dto.setOwnerId(owner.getId());

            dto.setOwnerName(owner.getName());

            dto.setEmail(owner.getEmail());

            String accountStatus =
                    organization != null &&
                    organization.getStatus() == OrganizationStatus.ACTIVE
                            ? "ACTIVE"
                            : "SUSPENDED";

            dto.setAccountStatus(accountStatus);

            dto.setOrganizationName(
                    organization != null
                            ? organization.getName()
                            : "No Organization");

            dto.setOrganizationStatus(
                    organization != null
                            ? organization.getStatus()
                            : null);

            dto.setCreatedAt(
                    organization != null
                            ? organization.getCreatedAt()
                            : null);

            response.add(dto);
        }

        return response;
    }

    @Override
    public AdminOwnerResponseDTO getOwner(
            String ownerId) {

        User owner =
                userRepository.findById(ownerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Owner not found"));

        Organization organization = null;

        if (owner.getOrganizationId() != null) {

            organization =
                    organizationRepository
                            .findById(owner.getOrganizationId())
                            .orElse(null);
        }

        AdminOwnerResponseDTO dto =
                new AdminOwnerResponseDTO();

        dto.setOwnerId(owner.getId());

        dto.setOwnerName(owner.getName());

        dto.setEmail(owner.getEmail());
        String accountStatus =
                organization != null &&
                organization.getStatus() == OrganizationStatus.ACTIVE
                        ? "ACTIVE"
                        : "SUSPENDED";

        dto.setAccountStatus(accountStatus);

        dto.setOrganizationName(
                organization != null
                        ? organization.getName()
                        : "No Organization");

        dto.setOrganizationStatus(
                organization != null
                        ? organization.getStatus()
                        : null);

        dto.setCreatedAt(
                organization != null
                        ? organization.getCreatedAt()
                        : null);

        return dto;
    }


}