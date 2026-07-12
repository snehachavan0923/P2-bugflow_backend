package com.bugflow.service.admin;

import java.util.Map;

import com.bugflow.dto.admin.AdminPaymentDTO;
import com.bugflow.dto.admin.AdminSubscriptionDTO;
import com.bugflow.dto.admin.PageResponseDTO;

public interface AdminSubscriptionService {

    PageResponseDTO<AdminSubscriptionDTO> subscriptions(
            String search,
            String plan,
            String status,
            String paymentStatus,
            int page,
            int size);

    AdminSubscriptionDTO detail(String id);

    PageResponseDTO<AdminPaymentDTO> payments(
            String search,
            String plan,
            String status,
            String from,
            String to,
            int page,
            int size);

    Map<String, Object> revenue();
}