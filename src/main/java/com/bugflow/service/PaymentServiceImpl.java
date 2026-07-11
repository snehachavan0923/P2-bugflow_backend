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
import com.bugflow.entity.SubscriptionPlan;
import com.bugflow.exception.BadRequestException;
import com.bugflow.repository.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String CURRENCY_INR = "INR";

    private final PaymentRepository paymentRepository;
    private final SubscriptionService subscriptionService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, SubscriptionService subscriptionService) {
        this.paymentRepository = paymentRepository;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public PaymentResponseDTO createSubscriptionPayment(String email, PaymentRequestDTO request) {
        SubscriptionPlan plan = parsePlan(request);

        // Validate before recording a successful payment. The subscription is not activated here.
        SubscriptionResponseDTO subscription = subscriptionService.validateSubscriptionUpgrade(email, plan);
        LocalDateTime now = LocalDateTime.now();

        Payment payment = new Payment();
        payment.setOrganizationId(subscription.getOrganizationId());
        payment.setOwnerId(subscription.getOwnerId());
        payment.setSubscriptionId(subscription.getId());
        payment.setPlan(plan);
        payment.setAmount(plan.getPrice());
        payment.setCurrency(CURRENCY_INR);
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
            subscriptionService.activateSubscription(email, plan);
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

    private SubscriptionPlan parsePlan(PaymentRequestDTO request) {
        if (request == null || request.getPlan() == null || request.getPlan().isBlank()) {
            throw new BadRequestException("Plan is required");
        }

        try {
            return SubscriptionPlan.valueOf(request.getPlan().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid plan selected");
        }
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
