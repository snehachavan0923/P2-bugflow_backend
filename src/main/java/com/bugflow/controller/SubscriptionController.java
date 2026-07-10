package com.bugflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugflow.dto.PlanOptionDTO;
import com.bugflow.dto.SubscriptionResponseDTO;
import com.bugflow.entity.SubscriptionPlan;
import com.bugflow.service.SubscriptionService;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<com.bugflow.dto.SubscriptionStatusDTO> getSubscriptionStatus(Authentication authentication) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionStatus(authentication.getName()));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('Owner')")
    public ResponseEntity<SubscriptionResponseDTO> getCurrentSubscription(Authentication authentication) {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(authentication.getName()));
    }

    @GetMapping("/plans")
    @PreAuthorize("hasRole('Owner')")
    public ResponseEntity<List<PlanOptionDTO>> getAvailablePlans(Authentication authentication) {
        return ResponseEntity.ok(subscriptionService.getAvailablePlans());
    }

    @PutMapping("/upgrade")
    @PreAuthorize("hasRole('Owner')")
    public ResponseEntity<SubscriptionResponseDTO> upgradeSubscription(
            Authentication authentication,
            @RequestBody SubscriptionPlan plan) {
        return ResponseEntity.ok(subscriptionService.upgradeSubscription(authentication.getName(), plan));
    }

    @PutMapping("/expire")
    @PreAuthorize("hasRole('Owner')")
    public ResponseEntity<SubscriptionResponseDTO> expireSubscription(Authentication authentication) {
        return ResponseEntity.ok(subscriptionService.expireSubscription(authentication.getName()));
    }

    @GetMapping("/usage")
    @PreAuthorize("hasRole('Owner')")
    public ResponseEntity<SubscriptionResponseDTO> getSubscriptionUsage(Authentication authentication) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionUsage(authentication.getName()));
    }

    @PutMapping("/admin/{organizationId}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<SubscriptionResponseDTO> adminUpgradeSubscription(
            @PathVariable String organizationId,
            @RequestBody SubscriptionPlan plan) {
        return ResponseEntity.ok(subscriptionService.upgradeSubscriptionByOrganizationId(organizationId, plan));
    }
}
