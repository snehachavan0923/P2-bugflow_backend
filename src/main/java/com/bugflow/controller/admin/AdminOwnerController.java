package com.bugflow.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bugflow.dto.admin.AdminOwnerResponseDTO;
import com.bugflow.service.admin.AdminOwnerService;

@RestController
@RequestMapping("/admin/owners")
public class AdminOwnerController {

    private final AdminOwnerService service;

    public AdminOwnerController(
            AdminOwnerService service) {

        this.service = service;
    }

    @GetMapping
    public List<AdminOwnerResponseDTO> getOwners() {

        return service.getOwners();
    }

    @GetMapping("/{ownerId}")
    public AdminOwnerResponseDTO getOwner(
            @PathVariable String ownerId) {

        return service.getOwner(ownerId);
    }

   
}