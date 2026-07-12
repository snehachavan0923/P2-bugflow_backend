package com.bugflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bugflow.dto.SubscriptionResponseDTO;
import com.bugflow.dto.UpgradeSubscriptionRequestDTO;
import com.bugflow.entity.Organization;
import com.bugflow.entity.SubscriptionPlan;
import com.bugflow.exception.BadRequestException;
import com.bugflow.entity.SubscriptionStatus;
import com.bugflow.entity.User;
import com.bugflow.repository.IssueRepository;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.PlanRepository;
import com.bugflow.repository.SubscriptionRepository;
import com.bugflow.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Test
    void upgradeSubscriptionShouldPersistPlanLimits() {
        User owner = new User();
        owner.setId("owner-1");
        owner.setOrganizationId("org-1");

        Organization organization = new Organization();
        organization.setId("org-1");
        organization.setOwnerId("owner-1");

        com.bugflow.entity.Subscription subscription = new com.bugflow.entity.Subscription();
        subscription.setId("sub-1");
        subscription.setOrganizationId("org-1");
        subscription.setOwnerId("owner-1");
        subscription.setPlan(SubscriptionPlan.FREE);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setProjectLimit(1);
        subscription.setMemberLimit(5);
        subscription.setIssueLimit(100);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(organizationRepository.findById("org-1")).thenReturn(Optional.of(organization));
        when(subscriptionRepository.findByOrganizationId("org-1")).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.save(any(com.bugflow.entity.Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpgradeSubscriptionRequestDTO request = new UpgradeSubscriptionRequestDTO();
        request.setPlan("STARTER");

        SubscriptionResponseDTO updated = subscriptionService.upgradeSubscription("owner@example.com", request);

        assertEquals("STARTER", updated.getPlan());
        assertEquals(5, updated.getProjectLimit());
        assertEquals(25, updated.getMemberLimit());
        assertNull(updated.getIssueLimit());
    }

    @Test
    void downgradeShouldFailWhenUsageExceedsTargetPlanLimits() {
        User owner = new User();
        owner.setId("owner-1");
        owner.setOrganizationId("org-1");

        Organization organization = new Organization();
        organization.setId("org-1");
        organization.setOwnerId("owner-1");

        com.bugflow.entity.Subscription subscription = new com.bugflow.entity.Subscription();
        subscription.setId("sub-1");
        subscription.setOrganizationId("org-1");
        subscription.setOwnerId("owner-1");
        subscription.setPlan(SubscriptionPlan.BUSINESS);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setProjectLimit(15);
        subscription.setMemberLimit(100);
        subscription.setIssueLimit(null);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(organizationRepository.findById("org-1")).thenReturn(Optional.of(organization));
        when(subscriptionRepository.findByOrganizationId("org-1")).thenReturn(Optional.of(subscription));
        when(projectRepository.countByOrganizationId("org-1")).thenReturn(12L);
        when(projectMemberRepository.countByOrganizationId("org-1")).thenReturn(30L);
        when(projectRepository.findByOrganizationId("org-1")).thenReturn(List.of());

        UpgradeSubscriptionRequestDTO request = new UpgradeSubscriptionRequestDTO();
        request.setPlan("STARTER");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> subscriptionService.upgradeSubscription("owner@example.com", request));
        assertEquals("Cannot change plan. Current organization exceeds Starter plan limits.", exception.getMessage());
        verify(subscriptionRepository, never()).save(any(com.bugflow.entity.Subscription.class));
    }

    @Test
    void enterprisePlanShouldBeRejected() {
        UpgradeSubscriptionRequestDTO request = new UpgradeSubscriptionRequestDTO();
        request.setPlan("ENTERPRISE");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> subscriptionService.upgradeSubscription("owner@example.com", request));
        assertTrue(exception.getMessage().contains("Enterprise"));
        verify(subscriptionRepository, never()).save(any(com.bugflow.entity.Subscription.class));
    }

    @Test
    void usageEndpointShouldNotPersistSubscriptionState() {
        User owner = new User();
        owner.setId("owner-1");
        owner.setOrganizationId("org-1");

        com.bugflow.entity.Subscription subscription = new com.bugflow.entity.Subscription();
        subscription.setId("sub-1");
        subscription.setOrganizationId("org-1");
        subscription.setOwnerId("owner-1");
        subscription.setPlan(SubscriptionPlan.FREE);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setProjectLimit(1);
        subscription.setMemberLimit(5);
        subscription.setIssueLimit(100);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(subscriptionRepository.findByOrganizationId("org-1")).thenReturn(Optional.of(subscription));
        when(projectRepository.countByOrganizationId("org-1")).thenReturn(2L);
        when(projectMemberRepository.countByOrganizationId("org-1")).thenReturn(3L);
        when(projectRepository.findByOrganizationId("org-1")).thenReturn(List.of());

        SubscriptionResponseDTO usage = subscriptionService.getSubscriptionUsage("owner@example.com");

        assertEquals(2, usage.getCurrentProjects());
        assertEquals(3, usage.getCurrentMembers());
        assertEquals(0, usage.getCurrentIssues());
        verify(subscriptionRepository, never()).save(any(com.bugflow.entity.Subscription.class));
    }

    @Test
    void usageEndpointShouldRemainReadOnlyWhenSubscriptionDoesNotExist() {
        User owner = new User();
        owner.setId("owner-1");
        owner.setOrganizationId("org-1");

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(subscriptionRepository.findByOrganizationId("org-1")).thenReturn(Optional.empty());
        when(projectRepository.countByOrganizationId("org-1")).thenReturn(0L);
        when(projectMemberRepository.countByOrganizationId("org-1")).thenReturn(0L);
        when(projectRepository.findByOrganizationId("org-1")).thenReturn(List.of());

        SubscriptionResponseDTO usage = subscriptionService.getSubscriptionUsage("owner@example.com");

        assertEquals("FREE", usage.getPlan());
        assertEquals(0, usage.getCurrentProjects());
        assertEquals(0, usage.getCurrentMembers());
        assertEquals(0, usage.getCurrentIssues());
        verify(subscriptionRepository, never()).save(any(com.bugflow.entity.Subscription.class));
    }
}
