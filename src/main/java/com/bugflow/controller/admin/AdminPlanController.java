package com.bugflow.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugflow.dto.admin.PlanDTO;
import com.bugflow.service.admin.AdminPlanService;

@RestController
@RequestMapping("/admin/plans")
@PreAuthorize("hasRole('Admin')")
public class AdminPlanController {

    private final AdminPlanService service;

    public AdminPlanController(AdminPlanService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PlanDTO>> getPlans() {
        return ResponseEntity.ok(service.getPlans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getPlan(@PathVariable String id) {
        return ResponseEntity.ok(service.getPlan(id));
    }

    @PostMapping
    public ResponseEntity<PlanDTO> createPlan(
            @RequestBody PlanDTO request) {

        return ResponseEntity.ok(
                service.createPlan(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanDTO> updatePlan(
            @PathVariable String id,
            @RequestBody PlanDTO request) {

        return ResponseEntity.ok(
                service.updatePlan(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable String id) {

        service.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}