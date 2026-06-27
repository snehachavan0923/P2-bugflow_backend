package com.bugflow.service.admin;

import java.util.List;

import com.bugflow.dto.admin.AdminOwnerResponseDTO;

public interface AdminOwnerService {

    List<AdminOwnerResponseDTO> getOwners();

    AdminOwnerResponseDTO getOwner(
            String ownerId);

}