package com.bugflow.service.admin;

import java.util.List;

import com.bugflow.dto.admin.AdminProjectResponseDTO;

public interface AdminProjectService {

    List<AdminProjectResponseDTO> getProjects();

    AdminProjectResponseDTO getProject(String projectId);

}