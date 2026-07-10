package com.bugflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bugflow.entity.Subscription;
import com.bugflow.entity.SubscriptionStatus;
import com.bugflow.exception.SubscriptionExpiredException;
import com.bugflow.exception.SubscriptionLimitExceededException;
import com.bugflow.repository.IssueRepository;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.SubscriptionRepository;

@Service
public class SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final IssueRepository issueRepository;

    public SubscriptionValidator(
            SubscriptionRepository subscriptionRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            IssueRepository issueRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.issueRepository = issueRepository;
    }

    public void checkProjectLimit(String organizationId) {
        Subscription subscription = subscriptionRepository.findByOrganizationId(organizationId).orElse(null);

        // If subscription is expired, block creation
        if (subscription != null && subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            throw new SubscriptionExpiredException("Your subscription has expired.");
        }

        Integer limit = subscription != null ? subscription.getProjectLimit() : null;

        if (limit == null) return; // unlimited

        long current = projectRepository.countByOrganizationId(organizationId);

        if (current >= limit) {
            throw new SubscriptionLimitExceededException("Project limit reached.");
        }
    }

    public void checkMemberLimit(String organizationId) {
        Subscription subscription = subscriptionRepository.findByOrganizationId(organizationId).orElse(null);

        if (subscription != null && subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            throw new SubscriptionExpiredException("Your subscription has expired.");
        }

        Integer limit = subscription != null ? subscription.getMemberLimit() : null;

        if (limit == null) return; // unlimited

        long current = projectMemberRepository.countByOrganizationId(organizationId);

        if (current >= limit) {
            throw new SubscriptionLimitExceededException("Member limit reached.");
        }
    }

    public void checkIssueLimit(String organizationId) {
        Subscription subscription = subscriptionRepository.findByOrganizationId(organizationId).orElse(null);

        if (subscription != null && subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            throw new SubscriptionExpiredException("Your subscription has expired.");
        }

        Integer limit = subscription != null ? subscription.getIssueLimit() : null;

        if (limit == null) return; // unlimited

        List<String> projectIds = projectRepository.findByOrganizationId(organizationId).stream()
                .map(p -> p.getId())
                .collect(Collectors.toList());

        long current = 0;
        if (!projectIds.isEmpty()) {
            current = issueRepository.countByProjectIdIn(projectIds);
        }

        if (current >= limit) {
            throw new SubscriptionLimitExceededException("Issue limit reached.");
        }
    }
}
