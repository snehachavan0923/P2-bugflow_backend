package com.bugflow.service;

import java.util.List;

import com.bugflow.dto.PaymentRequestDTO;
import com.bugflow.dto.PaymentResponseDTO;
import com.bugflow.entity.PaymentStatus;

public interface PaymentService {

    PaymentResponseDTO createSubscriptionPayment(String email, PaymentRequestDTO request);

    List<PaymentResponseDTO> getPaymentHistory(String email);

    List<PaymentResponseDTO> getPaymentsByOrganizationId(String organizationId);

    List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus paymentStatus);
}
