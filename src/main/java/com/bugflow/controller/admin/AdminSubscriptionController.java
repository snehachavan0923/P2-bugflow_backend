package com.bugflow.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bugflow.dto.admin.AdminPaymentDTO;
import com.bugflow.dto.admin.AdminRevenueDashboardDTO;
import com.bugflow.dto.admin.AdminSubscriptionDTO;
import com.bugflow.dto.admin.PageResponseDTO;
import com.bugflow.service.admin.AdminSubscriptionService;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('Admin')")
public class AdminSubscriptionController {

    private final AdminSubscriptionService service;

    public AdminSubscriptionController(
            AdminSubscriptionService service) {

        this.service = service;
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<PageResponseDTO<AdminSubscriptionDTO>>
            subscriptions(

                    @RequestParam(required = false)
                    String search,

                    @RequestParam(required = false)
                    String plan,

                    @RequestParam(required = false)
                    String status,

                    @RequestParam(required = false)
                    String paymentStatus,

                    @RequestParam(defaultValue = "0")
                    int page,

                    @RequestParam(defaultValue = "20")
                    int size) {

        return ResponseEntity.ok(
                service.subscriptions(
                        search,
                        plan,
                        status,
                        paymentStatus,
                        page,
                        size));
    }

    @GetMapping("/subscriptions/{id}")
    public ResponseEntity<AdminSubscriptionDTO>
            detail(@PathVariable String id) {

        return ResponseEntity.ok(
                service.detail(id));
    }

    @GetMapping("/payments")
    public ResponseEntity<PageResponseDTO<AdminPaymentDTO>>
            payments(

                    @RequestParam(required = false)
                    String search,

                    @RequestParam(required = false)
                    String plan,

                    @RequestParam(required = false)
                    String status,

                    @RequestParam(required = false)
                    String from,

                    @RequestParam(required = false)
                    String to,

                    @RequestParam(defaultValue = "0")
                    int page,

                    @RequestParam(defaultValue = "20")
                    int size) {

        return ResponseEntity.ok(
                service.payments(
                        search,
                        plan,
                        status,
                        from,
                        to,
                        page,
                        size));
    }

    @GetMapping("/revenue/dashboard")
    public ResponseEntity<AdminRevenueDashboardDTO> revenue(
            @RequestParam(required = false)
            String from,

            @RequestParam(required = false)
            String to,

            @RequestParam(required = false)
            String plan) {

        return ResponseEntity.ok(
                service.revenue(from, to, plan));
    }
}