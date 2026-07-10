package com.bugflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bugflow.dto.SubscriptionResponseDTO;
import com.bugflow.entity.Organization;
import com.bugflow.entity.SubscriptionPlan;
import com.bugflow.entity.SubscriptionStatus;
import com.bugflow.entity.User;
import com.bugflow.repository.IssueRepository;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;
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

        SubscriptionResponseDTO updated = subscriptionService.upgradeSubscription("owner@example.com", SubscriptionPlan.STARTER);

        assertEquals(SubscriptionPlan.STARTER, updated.getPlan());
        assertEquals(5, updated.getProjectLimit());
        assertEquals(25, updated.getMemberLimit());
        assertNull(updated.getIssueLimit());
    }
}
