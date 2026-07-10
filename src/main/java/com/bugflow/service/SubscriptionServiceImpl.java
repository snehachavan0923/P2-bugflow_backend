package com.bugflow.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bugflow.dto.PlanOptionDTO;
import com.bugflow.dto.SubscriptionResponseDTO;
import com.bugflow.entity.Organization;
import com.bugflow.entity.Subscription;
import com.bugflow.entity.SubscriptionPlan;
import com.bugflow.entity.SubscriptionStatus;
import com.bugflow.entity.User;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.IssueRepository;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.SubscriptionRepository;
import com.bugflow.repository.UserRepository;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final IssueRepository issueRepository;

    public SubscriptionServiceImpl(
            SubscriptionRepository subscriptionRepository,
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            IssueRepository issueRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.issueRepository = issueRepository;
    }

    @Override
    public SubscriptionResponseDTO getCurrentSubscription(String email) {
        User user = findUser(email);
        Subscription subscription = findSubscriptionForUser(user);
        return mapToDto(subscription);
    }

    @Override
    public List<PlanOptionDTO> getAvailablePlans() {
        List<PlanOptionDTO> plans = new ArrayList<>();
        for (SubscriptionPlan plan : SubscriptionPlan.values()) {
            PlanOptionDTO dto = new PlanOptionDTO();
            dto.setName(plan.name());
            dto.setDisplayName(plan.getDisplayName());
            dto.setProjectLimit(plan.getProjectLimit());
            dto.setMemberLimit(plan.getMemberLimit());
            dto.setIssueLimit(plan.getIssueLimit());
            dto.setUnlimited(plan.getIssueLimit() == null && plan != SubscriptionPlan.FREE);
            plans.add(dto);
        }
        return plans;
    }

    @Override
    public SubscriptionResponseDTO upgradeSubscription(String email, SubscriptionPlan plan) {
        if (plan == null) {
            throw new BadRequestException("Plan is required");
        }

        User user = findUser(email);
        if (user.getOrganizationId() == null) {
            throw new BadRequestException("Organization is required for subscription");
        }

        Organization organization = organizationRepository.findById(user.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        if (!organization.getOwnerId().equals(user.getId())) {
            throw new BadRequestException("Only organization owner can manage subscriptions");
        }

        return applyPlanToSubscription(findSubscriptionForUser(user), plan);
    }

    @Override
    public SubscriptionResponseDTO upgradeSubscriptionByOrganizationId(String organizationId, SubscriptionPlan plan) {
        if (plan == null) {
            throw new BadRequestException("Plan is required");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByOrganizationId(organization.getId());
        Subscription subscription = subscriptionOpt.orElseGet(() -> createDefaultSubscription(organization));
        return applyPlanToSubscription(subscription, plan);
    }

    @Override
    public SubscriptionResponseDTO expireSubscription(String email) {
        User user = findUser(email);
        Subscription subscription = findSubscriptionForUser(user);
        subscription.setStatus(SubscriptionStatus.EXPIRED);
        subscription.setEndDate(LocalDateTime.now());
        subscription.setUpdatedAt(LocalDateTime.now());
        Subscription saved = subscriptionRepository.save(subscription);
        return mapToDto(saved);
    }

    @Override
    public SubscriptionResponseDTO getSubscriptionUsage(String email) {
        User user = findUser(email);
        Subscription subscription = findSubscriptionForUser(user);

        long projectCount = projectRepository.countByOrganizationId(user.getOrganizationId());
        long memberCount = projectMemberRepository.countByOrganizationId(user.getOrganizationId());
        long issueCount = issueRepository.countByProjectIdIn(projectRepository.findByOrganizationId(user.getOrganizationId()).stream()
                .map(project -> project.getId())
                .toList());

        subscription.setCurrentProjects((int) projectCount);
        subscription.setCurrentMembers((int) memberCount);
        subscription.setCurrentIssues((int) issueCount);
        subscription.setUpdatedAt(LocalDateTime.now());
        Subscription saved = subscriptionRepository.save(subscription);
        return mapToDto(saved);
    }

    @Override
    public com.bugflow.dto.SubscriptionStatusDTO getSubscriptionStatus(String email) {
        User user = findUser(email);
        Subscription subscription = findSubscriptionForUser(user);
        com.bugflow.dto.SubscriptionStatusDTO dto = new com.bugflow.dto.SubscriptionStatusDTO();
        dto.setStatus(subscription.getStatus());
        return dto;
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Subscription findSubscriptionForUser(User user) {
        if (user.getOrganizationId() == null) {
            throw new BadRequestException("Organization is required for subscription");
        }

        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByOrganizationId(user.getOrganizationId());
        if (subscriptionOpt.isPresent()) {
            return subscriptionOpt.get();
        }

        Subscription subscription = new Subscription();
        subscription.setOrganizationId(user.getOrganizationId());
        subscription.setOwnerId(user.getId());
        subscription.setPlan(SubscriptionPlan.FREE);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setProjectLimit(resolveProjectLimit(SubscriptionPlan.FREE));
        subscription.setMemberLimit(resolveMemberLimit(SubscriptionPlan.FREE));
        subscription.setIssueLimit(resolveIssueLimit(SubscriptionPlan.FREE));
        subscription.setCurrentProjects(0);
        subscription.setCurrentMembers(0);
        subscription.setCurrentIssues(0);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setUpdatedAt(LocalDateTime.now());
        return subscriptionRepository.save(subscription);
    }

    private SubscriptionResponseDTO applyPlanToSubscription(Subscription subscription, SubscriptionPlan plan) {
        subscription.setPlan(plan);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setProjectLimit(resolveProjectLimit(plan));
        subscription.setMemberLimit(resolveMemberLimit(plan));
        subscription.setIssueLimit(resolveIssueLimit(plan));
        subscription.setUpdatedAt(LocalDateTime.now());

        if (subscription.getStartDate() == null) {
            subscription.setStartDate(LocalDateTime.now());
        }
        subscription.setEndDate(null);

        Subscription saved = subscriptionRepository.save(subscription);
        return mapToDto(saved);
    }

    private Subscription createDefaultSubscription(Organization organization) {
        Subscription subscription = new Subscription();
        subscription.setOrganizationId(organization.getId());
        subscription.setOwnerId(organization.getOwnerId());
        subscription.setPlan(SubscriptionPlan.FREE);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setProjectLimit(resolveProjectLimit(SubscriptionPlan.FREE));
        subscription.setMemberLimit(resolveMemberLimit(SubscriptionPlan.FREE));
        subscription.setIssueLimit(resolveIssueLimit(SubscriptionPlan.FREE));
        subscription.setCurrentProjects(0);
        subscription.setCurrentMembers(0);
        subscription.setCurrentIssues(0);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setUpdatedAt(LocalDateTime.now());
        return subscriptionRepository.save(subscription);
    }

    private Integer resolveProjectLimit(SubscriptionPlan plan) {
        return plan.getProjectLimit();
    }

    private Integer resolveMemberLimit(SubscriptionPlan plan) {
        return plan.getMemberLimit();
    }

    private Integer resolveIssueLimit(SubscriptionPlan plan) {
        return plan.getIssueLimit();
    }

    private SubscriptionResponseDTO mapToDto(Subscription subscription) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setId(subscription.getId());
        dto.setOrganizationId(subscription.getOrganizationId());
        dto.setOwnerId(subscription.getOwnerId());
        dto.setPlan(subscription.getPlan());
        dto.setStatus(subscription.getStatus());
        dto.setProjectLimit(subscription.getProjectLimit());
        dto.setMemberLimit(subscription.getMemberLimit());
        dto.setIssueLimit(subscription.getIssueLimit());
        dto.setCurrentProjects(subscription.getCurrentProjects());
        dto.setCurrentMembers(subscription.getCurrentMembers());
        dto.setCurrentIssues(subscription.getCurrentIssues());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());
        return dto;
    }
}
