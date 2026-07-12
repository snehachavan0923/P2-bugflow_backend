package com.bugflow.service.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bugflow.dto.admin.AdminOrganizationResponseDTO;
import com.bugflow.entity.Organization;
import com.bugflow.entity.OrganizationStatus;
import com.bugflow.entity.User;
import com.bugflow.entity.Subscription;
import com.bugflow.entity.Payment;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.UserRepository;
import com.bugflow.repository.SubscriptionRepository;
import com.bugflow.repository.PaymentRepository;

@Service
public class AdminOrganizationServiceImpl
        implements AdminOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    public AdminOrganizationServiceImpl(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            SubscriptionRepository subscriptionRepository,
            PaymentRepository paymentRepository) {

        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<AdminOrganizationResponseDTO> getOrganizations() {

        return organizationRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdminOrganizationResponseDTO getOrganization(
            String organizationId) {

        Organization organization =
                organizationRepository.findById(organizationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organization not found"));

        return mapToDto(organization);
    }

    @Override
    public void suspendOrganization(
            String organizationId) {

        Organization organization =
                organizationRepository.findById(organizationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organization not found"));

        organization.setStatus(
                OrganizationStatus.SUSPENDED);

        organizationRepository.save(organization);
    }

    @Override
    public void activateOrganization(
            String organizationId) {

        Organization organization =
                organizationRepository.findById(organizationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organization not found"));

        organization.setStatus(
                OrganizationStatus.ACTIVE);

        organizationRepository.save(organization);
    }

    private AdminOrganizationResponseDTO mapToDto(
            Organization organization) {

        User owner =
                userRepository.findById(
                        organization.getOwnerId())
                .orElse(null);

        AdminOrganizationResponseDTO dto =
                new AdminOrganizationResponseDTO();

        dto.setOrganizationId(
                organization.getId());

        dto.setOrganizationName(
                organization.getName());

        dto.setOwnerName(
                owner != null
                        ? owner.getName()
                        : "Unknown");

        dto.setOwnerEmail(
                owner != null
                        ? owner.getEmail()
                        : "-");

        dto.setTotalProjects(
                projectRepository.countByOrganizationId(
                        organization.getId()));

        dto.setTotalMembers(
                projectMemberRepository.countByOrganizationId(
                        organization.getId()));

        dto.setOrganizationStatus(
                organization.getStatus());

        dto.setCreatedAt(
                organization.getCreatedAt());

        Subscription subscription = subscriptionRepository.findByOrganizationId(organization.getId()).orElse(null);
        if (subscription != null) {
            dto.setSubscriptionPlan(subscription.getPlan());
            dto.setSubscriptionStatus(subscription.getStatus() == null ? null : subscription.getStatus().name());
            dto.setExpiryDate(subscription.getEndDate());
        }
        Payment payment = paymentRepository.findByOrganizationIdOrderByPaymentDateDesc(organization.getId())
                .stream().findFirst().orElse(null);
        dto.setPaymentStatus(payment == null || payment.getPaymentStatus() == null
                ? null : payment.getPaymentStatus().name());

        return dto;
    }
}
