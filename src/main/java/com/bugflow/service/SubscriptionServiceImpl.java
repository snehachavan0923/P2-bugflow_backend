package com.bugflow.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bugflow.dto.PlanOptionDTO;
import com.bugflow.dto.SubscriptionResponseDTO;
import com.bugflow.dto.UpgradeSubscriptionRequestDTO;
import com.bugflow.entity.Organization;
import com.bugflow.entity.Plan;
import com.bugflow.entity.Project;
import com.bugflow.entity.Subscription;
import com.bugflow.entity.SubscriptionPlan;
import com.bugflow.entity.SubscriptionStatus;
import com.bugflow.entity.User;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.IssueRepository;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.PlanRepository;
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
    private final PlanRepository planRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
            OrganizationRepository organizationRepository, UserRepository userRepository,
            ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository,
            IssueRepository issueRepository, PlanRepository planRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.issueRepository = issueRepository;
        this.planRepository = planRepository;
    }

    @Override
    public SubscriptionResponseDTO getCurrentSubscription(String email) {
        Subscription subscription = findSubscriptionForUser(findUser(email));
        return mapToDto(subscription, findPlan(subscription.getPlan(), false));
    }

    @Override
    public List<PlanOptionDTO> getAvailablePlans() {
        return planRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream().map(plan -> {
            PlanOptionDTO dto = new PlanOptionDTO();
            dto.setName(plan.getName());
            dto.setDisplayName(plan.getDisplayName());

            dto.setPrice(plan.getPrice());
            dto.setCurrency(plan.getCurrency());

            dto.setBillingPeriod(plan.getBillingPeriod());
            dto.setDescription(plan.getDescription());

            dto.setDurationDays(plan.getDurationDays());

            dto.setProjectLimit(plan.getProjectLimit());
            dto.setMemberLimit(plan.getMemberLimit());
            dto.setIssueLimit(plan.getIssueLimit());

            dto.setHighlight(plan.isHighlight());
            dto.setAccentColor(plan.getAccentColor());

            dto.setFeatures(plan.getFeatures());

            dto.setUnlimited(
                    plan.getProjectLimit() == null
                    && plan.getMemberLimit() == null
                    && plan.getIssueLimit() == null);
            return dto;
        }).toList();
    }

    @Override
    public SubscriptionResponseDTO upgradeSubscription(String email, UpgradeSubscriptionRequestDTO request) {
        return activateSubscription(email, request == null ? null : request.getPlan());
    }

    @Override
    public SubscriptionResponseDTO validateSubscriptionUpgrade(String email, SubscriptionPlan plan) {
        return validateSubscriptionUpgrade(email, plan == null ? null : plan.name());
    }

    @Override
    public SubscriptionResponseDTO validateSubscriptionUpgrade(String email, String planName) {
        Plan plan = findPlan(planName, true);
        if ("ENTERPRISE".equalsIgnoreCase(plan.getName())) {
            throw new BadRequestException("Enterprise plans must be handled through sales contact");
        }

        User user = findUser(email);
        if (user.getOrganizationId() == null) {
            throw new BadRequestException("Organization is required for subscription");
        }
        Organization organization = organizationRepository.findById(user.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        if (!user.getId().equals(organization.getOwnerId())) {
            throw new BadRequestException("Only organization owner can manage subscriptions");
        }

        Subscription subscription = findSubscriptionForUser(user);
        validatePlanChange(subscription, plan);
        return mapToDto(subscription, findPlan(subscription.getPlan(), false));
    }

    @Override
    public SubscriptionResponseDTO activateSubscription(String email, SubscriptionPlan plan) {
        return activateSubscription(email, plan == null ? null : plan.name());
    }

    @Override
    public SubscriptionResponseDTO activateSubscription(String email, String planName) {
        Plan plan = findPlan(planName, true);
        validateSubscriptionUpgrade(email, plan.getName());
        return applyPlanToSubscription(findSubscriptionForUser(findUser(email)), plan);
    }

    @Override
    public SubscriptionResponseDTO upgradeSubscriptionByOrganizationId(String organizationId, SubscriptionPlan plan) {
        if (plan == null) throw new BadRequestException("Plan is required");
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        Subscription subscription = subscriptionRepository.findByOrganizationId(organization.getId())
                .orElseGet(() -> createDefaultSubscription(organization));
        return applyPlanToSubscription(subscription, findPlan(plan.name(), false));
    }

    @Override
    public SubscriptionResponseDTO expireSubscription(String email) {
        Subscription subscription = findSubscriptionForUser(findUser(email));
        LocalDateTime now = LocalDateTime.now();
        subscription.setStatus(SubscriptionStatus.EXPIRED);
        subscription.setEndDate(now);
        subscription.setUpdatedAt(now);
        return mapToDto(subscriptionRepository.save(subscription), findPlan(subscription.getPlan(), false));
    }

    @Override
    public SubscriptionResponseDTO getSubscriptionUsage(String email) {
        User user = findUser(email);
        Subscription subscription = findSubscriptionReadOnly(user);
        Plan plan = findPlan(subscription.getPlan(), false);
        SubscriptionResponseDTO dto = mapToDto(subscription, plan);
        dto.setCurrentProjects((int) projectRepository.countByOrganizationId(user.getOrganizationId()));
        dto.setCurrentMembers((int) projectMemberRepository.countByOrganizationId(user.getOrganizationId()));
        dto.setCurrentIssues((int) countIssuesForOrganization(user.getOrganizationId()));
        return dto;
    }

    @Override
    public com.bugflow.dto.SubscriptionStatusDTO getSubscriptionStatus(String email) {
        com.bugflow.dto.SubscriptionStatusDTO dto = new com.bugflow.dto.SubscriptionStatusDTO();
        dto.setStatus(findSubscriptionForUser(findUser(email)).getStatus());
        return dto;
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Subscription findSubscriptionForUser(User user) {
        if (user.getOrganizationId() == null) throw new BadRequestException("Organization is required for subscription");
        return subscriptionRepository.findByOrganizationId(user.getOrganizationId())
                .orElseGet(() -> createDefaultSubscription(user.getOrganizationId(), user.getId()));
    }

    private Subscription findSubscriptionReadOnly(User user) {
        if (user.getOrganizationId() == null) throw new BadRequestException("Organization is required for subscription");
        return subscriptionRepository.findByOrganizationId(user.getOrganizationId())
                .orElseGet(() -> defaultSubscription(user.getOrganizationId(), user.getId()));
    }

    private Subscription createDefaultSubscription(Organization organization) {
        return subscriptionRepository.save(defaultSubscription(organization.getId(), organization.getOwnerId()));
    }

    private Subscription createDefaultSubscription(String organizationId, String ownerId) {
        return subscriptionRepository.save(defaultSubscription(organizationId, ownerId));
    }

    private Subscription defaultSubscription(String organizationId, String ownerId) {
        Plan free = findPlan("FREE", false);
        Subscription subscription = new Subscription();
        subscription.setOrganizationId(organizationId);
        subscription.setOwnerId(ownerId);
        subscription.setPlan(free.getName());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setProjectLimit(free.getProjectLimit());
        subscription.setMemberLimit(free.getMemberLimit());
        subscription.setIssueLimit(free.getIssueLimit());
        subscription.setCurrentProjects(0);
        subscription.setCurrentMembers(0);
        subscription.setCurrentIssues(0);
        LocalDateTime now = LocalDateTime.now();
        subscription.setStartDate(now);
        subscription.setCreatedAt(now);
        subscription.setUpdatedAt(now);
        return subscription;
    }

    private void validatePlanChange(Subscription subscription, Plan targetPlan) {
        long projects = projectRepository.countByOrganizationId(subscription.getOrganizationId());
        long members = projectMemberRepository.countByOrganizationId(subscription.getOrganizationId());
        long issues = countIssuesForOrganization(subscription.getOrganizationId());
        if ((targetPlan.getProjectLimit() != null && projects > targetPlan.getProjectLimit())
                || (targetPlan.getMemberLimit() != null && members > targetPlan.getMemberLimit())
                || (targetPlan.getIssueLimit() != null && issues > targetPlan.getIssueLimit())) {
            throw new BadRequestException("Cannot change plan. Current organization exceeds "
                    + targetPlan.getDisplayName() + " plan limits.");
        }
    }

    private long countIssuesForOrganization(String organizationId) {
        List<Project> projects = projectRepository.findByOrganizationId(organizationId);
        return projects == null || projects.isEmpty() ? 0
                : issueRepository.countByProjectIdIn(projects.stream().map(Project::getId).toList());
    }

    private SubscriptionResponseDTO applyPlanToSubscription(Subscription subscription, Plan plan) {
        LocalDateTime now = LocalDateTime.now();
        subscription.setPlan(plan.getName());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setProjectLimit(plan.getProjectLimit());
        subscription.setMemberLimit(plan.getMemberLimit());
        subscription.setIssueLimit(plan.getIssueLimit());
        subscription.setStartDate(now);
        subscription.setEndDate(plan.getDurationDays() == null ? null : now.plusDays(plan.getDurationDays()));
        subscription.setUpdatedAt(now);
        return mapToDto(subscriptionRepository.save(subscription), plan);
    }

    private Plan findPlan(String name, boolean requireActive) {
        if (name == null || name.isBlank()) throw new BadRequestException("Plan is required");
        Optional<Plan> plan = planRepository.findByNameIgnoreCase(name.trim());
        if (plan.isPresent()) {
            if (requireActive && !plan.get().isActive()) throw new BadRequestException("Selected plan is inactive");
            return plan.get();
        }
        // Legacy fallback keeps existing subscriptions usable until their plans are migrated to MongoDB.
        try {
            SubscriptionPlan legacy = SubscriptionPlan.valueOf(name.trim().toUpperCase());
            Plan fallback = new Plan();
            fallback.setName(legacy.name());
            fallback.setDisplayName(legacy.getDisplayName());
            fallback.setPrice(legacy.getPrice());
            fallback.setDurationDays(legacy.getDurationDays());
            fallback.setProjectLimit(legacy.getProjectLimit());
            fallback.setMemberLimit(legacy.getMemberLimit());
            fallback.setIssueLimit(legacy.getIssueLimit());
            return fallback;
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid plan selected");
        }
    }

    private SubscriptionResponseDTO mapToDto(Subscription subscription, Plan plan) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setId(subscription.getId());
        dto.setOrganizationId(subscription.getOrganizationId());
        dto.setOwnerId(subscription.getOwnerId());
        dto.setPlan(subscription.getPlan());
        dto.setStatus(subscription.getStatus());
        dto.setProjectLimit(plan.getProjectLimit());
        dto.setMemberLimit(plan.getMemberLimit());
        dto.setIssueLimit(plan.getIssueLimit());
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
