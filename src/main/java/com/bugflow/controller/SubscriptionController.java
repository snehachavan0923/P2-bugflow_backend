package com.bugflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugflow.dto.PlanOptionDTO;
import com.bugflow.dto.PaymentRequestDTO;
import com.bugflow.dto.PaymentResponseDTO;
import com.bugflow.dto.SubscriptionResponseDTO;
import com.bugflow.dto.UpgradeSubscriptionRequestDTO;
import com.bugflow.entity.SubscriptionPlan;
import com.bugflow.service.SubscriptionService;
import com.bugflow.service.PaymentService;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;

    public SubscriptionController(SubscriptionService subscriptionService, PaymentService paymentService) {
        this.subscriptionService = subscriptionService;
        this.paymentService = paymentService;
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
    public ResponseEntity<List<PlanOptionDTO>> getAvailablePlans(Authentication authentication) {
        return ResponseEntity.ok(subscriptionService.getAvailablePlans());
    }

    @PutMapping("/upgrade")
    @PreAuthorize("hasRole('Owner')")
    public ResponseEntity<SubscriptionResponseDTO> upgradeSubscription(
            Authentication authentication,
            @RequestBody UpgradeSubscriptionRequestDTO request) {
        PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
        paymentRequest.setPlan(request == null ? null : request.getPlan());
        paymentService.createSubscriptionPayment(authentication.getName(), paymentRequest);
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(authentication.getName()));
    }

    @PostMapping("/payment")
    @PreAuthorize("hasRole('Owner')")
    public ResponseEntity<PaymentResponseDTO> createPayment(
            Authentication authentication,
            @RequestBody PaymentRequestDTO request) {
        return ResponseEntity.ok(paymentService.createSubscriptionPayment(authentication.getName(), request));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasRole('Owner')")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentHistory(Authentication authentication) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(authentication.getName()));
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
