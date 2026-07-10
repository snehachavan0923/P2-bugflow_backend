package com.bugflow.service;

import java.util.List;

import com.bugflow.dto.PlanOptionDTO;
import com.bugflow.dto.SubscriptionResponseDTO;
import com.bugflow.dto.UpgradeSubscriptionRequestDTO;
import com.bugflow.entity.SubscriptionPlan;

public interface SubscriptionService {

    SubscriptionResponseDTO getCurrentSubscription(String email);

    List<PlanOptionDTO> getAvailablePlans();

    SubscriptionResponseDTO upgradeSubscription(String email, UpgradeSubscriptionRequestDTO request);

    SubscriptionResponseDTO upgradeSubscriptionByOrganizationId(String organizationId, SubscriptionPlan plan);

    SubscriptionResponseDTO expireSubscription(String email);

    SubscriptionResponseDTO getSubscriptionUsage(String email);

    com.bugflow.dto.SubscriptionStatusDTO getSubscriptionStatus(String email);
}
