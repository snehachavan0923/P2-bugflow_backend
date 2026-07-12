package com.bugflow.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bugflow.dto.PaymentRequestDTO;
import com.bugflow.dto.PaymentResponseDTO;
import com.bugflow.dto.SubscriptionResponseDTO;
import com.bugflow.entity.Payment;
import com.bugflow.entity.PaymentMethod;
import com.bugflow.entity.PaymentStatus;
import com.bugflow.entity.Plan;
import com.bugflow.exception.BadRequestException;
import com.bugflow.repository.PaymentRepository;
import com.bugflow.repository.PlanRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionService subscriptionService;
    private final PlanRepository planRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, SubscriptionService subscriptionService,
            PlanRepository planRepository) {
        this.paymentRepository = paymentRepository;
        this.subscriptionService = subscriptionService;
        this.planRepository = planRepository;
    }

    @Override
    public PaymentResponseDTO createSubscriptionPayment(String email, PaymentRequestDTO request) {
        Plan plan = findPlan(request);

        // Validate before recording a successful payment. The subscription is not activated here.
        SubscriptionResponseDTO subscription = subscriptionService.validateSubscriptionUpgrade(email, plan.getName());
        LocalDateTime now = LocalDateTime.now();

        Payment payment = new Payment();
        payment.setOrganizationId(subscription.getOrganizationId());
        payment.setOwnerId(subscription.getOwnerId());
        payment.setSubscriptionId(subscription.getId());
        payment.setPlan(plan.getName());
        payment.setAmount(plan.getPrice());
        payment.setCurrency(plan.getCurrency());
        payment.setPaymentMethod(PaymentMethod.MANUAL);
        // Replace this assignment with gateway verification when a provider is introduced.
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentDate(now);
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);

        Payment savedPayment = paymentRepository.save(payment);

        try {
            // This must remain after payment persistence so a subscription is never activated first.
            subscriptionService.activateSubscription(email, plan.getName());
            return mapToDto(savedPayment);
        } catch (RuntimeException ex) {
            savedPayment.setPaymentStatus(PaymentStatus.FAILED);
            savedPayment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(savedPayment);
            throw ex;
        }
    }

    @Override
    public List<PaymentResponseDTO> getPaymentHistory(String email) {
        SubscriptionResponseDTO subscription = subscriptionService.getCurrentSubscription(email);
        return paymentRepository.findByOwnerIdOrderByPaymentDateDesc(subscription.getOwnerId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByOrganizationId(String organizationId) {
        return paymentRepository.findByOrganizationIdOrderByPaymentDateDesc(organizationId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus paymentStatus) {
        return paymentRepository.findByPaymentStatusOrderByPaymentDateDesc(paymentStatus)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private Plan findPlan(PaymentRequestDTO request) {
        if (request == null || request.getPlan() == null || request.getPlan().isBlank()) {
            throw new BadRequestException("Plan is required");
        }

        Plan plan = planRepository.findByNameIgnoreCase(request.getPlan().trim())
                .orElseThrow(() -> new BadRequestException("Invalid plan selected"));
        if (!plan.isActive()) throw new BadRequestException("Selected plan is inactive");
        return plan;
    }

    private String generateTransactionId() {
        return "MAN-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private PaymentResponseDTO mapToDto(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setOrganizationId(payment.getOrganizationId());
        dto.setOwnerId(payment.getOwnerId());
        dto.setSubscriptionId(payment.getSubscriptionId());
        dto.setPlan(payment.getPlan());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }
}
