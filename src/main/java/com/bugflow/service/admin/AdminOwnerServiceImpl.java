package com.bugflow.service.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bugflow.dto.admin.AdminOwnerResponseDTO;
import com.bugflow.entity.Organization;
import com.bugflow.entity.OrganizationStatus;
import com.bugflow.entity.Role;
import com.bugflow.entity.User;
import com.bugflow.entity.Subscription;
import com.bugflow.entity.Payment;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.UserRepository;
import com.bugflow.repository.SubscriptionRepository;
import com.bugflow.repository.PaymentRepository;

@Service
public class AdminOwnerServiceImpl
        implements AdminOwnerService {

    private final UserRepository userRepository;

    private final OrganizationRepository organizationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    public AdminOwnerServiceImpl(
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            SubscriptionRepository subscriptionRepository,
            PaymentRepository paymentRepository) {

        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<AdminOwnerResponseDTO> getOwners() {

        List<User> owners =
                userRepository.findByRole(Role.Owner);

        List<AdminOwnerResponseDTO> response =
                new ArrayList<>();

        for (User owner : owners) {

            Organization organization = null;

            if (owner.getOrganizationId() != null) {

                organization =
                        organizationRepository
                                .findById(owner.getOrganizationId())
                                .orElse(null);
            }

            AdminOwnerResponseDTO dto =
                    new AdminOwnerResponseDTO();

            dto.setOwnerId(owner.getId());

            dto.setOwnerName(owner.getName());

            dto.setEmail(owner.getEmail());

            String accountStatus =
                    organization != null &&
                    organization.getStatus() == OrganizationStatus.ACTIVE
                            ? "ACTIVE"
                            : "SUSPENDED";

            dto.setAccountStatus(accountStatus);

            dto.setOrganizationName(
                    organization != null
                            ? organization.getName()
                            : "No Organization");

            dto.setOrganizationStatus(
                    organization != null
                            ? organization.getStatus()
                            : null);

            dto.setCreatedAt(owner.getCreatedAt());
            addSubscriptionDetails(dto, owner.getOrganizationId());

            response.add(dto);
        }

        return response;
    }

    @Override
    public AdminOwnerResponseDTO getOwner(
            String ownerId) {

        User owner =
                userRepository.findById(ownerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Owner not found"));

        Organization organization = null;

        if (owner.getOrganizationId() != null) {

            organization =
                    organizationRepository
                            .findById(owner.getOrganizationId())
                            .orElse(null);
        }

        AdminOwnerResponseDTO dto =
                new AdminOwnerResponseDTO();

        dto.setOwnerId(owner.getId());

        dto.setOwnerName(owner.getName());

        dto.setEmail(owner.getEmail());
        String accountStatus =
                organization != null &&
                organization.getStatus() == OrganizationStatus.ACTIVE
                        ? "ACTIVE"
                        : "SUSPENDED";

        dto.setAccountStatus(accountStatus);

        dto.setOrganizationName(
                organization != null
                        ? organization.getName()
                        : "No Organization");

        dto.setOrganizationStatus(
                organization != null
                        ? organization.getStatus()
                        : null);

        dto.setCreatedAt(owner.getCreatedAt());
        addSubscriptionDetails(dto, owner.getOrganizationId());

        return dto;
    }

    private void addSubscriptionDetails(AdminOwnerResponseDTO dto, String organizationId) {
        if (organizationId == null) return;
        Subscription subscription = subscriptionRepository.findByOrganizationId(organizationId).orElse(null);
        if (subscription != null) {
            dto.setSubscriptionPlan(subscription.getPlan());
            dto.setSubscriptionStatus(subscription.getStatus() == null ? null : subscription.getStatus().name());
            dto.setExpiryDate(subscription.getEndDate());
        }
        Payment payment = paymentRepository.findByOrganizationIdOrderByPaymentDateDesc(organizationId)
                .stream().findFirst().orElse(null);
        dto.setPaymentStatus(payment == null || payment.getPaymentStatus() == null
                ? null : payment.getPaymentStatus().name());
    }


}
