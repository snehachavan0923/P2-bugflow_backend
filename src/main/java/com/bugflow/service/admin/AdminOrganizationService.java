package com.bugflow.service.admin;

import java.util.List;

import com.bugflow.dto.admin.AdminOrganizationResponseDTO;

public interface AdminOrganizationService {

    List<AdminOrganizationResponseDTO> getOrganizations();

    AdminOrganizationResponseDTO getOrganization(
            String organizationId);

    void suspendOrganization(
            String organizationId);

    void activateOrganization(
            String organizationId);

}