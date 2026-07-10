package com.bugflow.service.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bugflow.entity.Subscription;
import com.bugflow.entity.SubscriptionStatus;
import com.bugflow.repository.SubscriptionRepository;

@Component
public class SubscriptionExpiryScheduler {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionExpiryScheduler(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void expireSubscriptions() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Subscription subscription : subscriptions) {
            if (subscription.getStatus() == SubscriptionStatus.ACTIVE
                    && subscription.getEndDate() != null
                    && !subscription.getEndDate().isAfter(now)) {
                subscription.setStatus(SubscriptionStatus.EXPIRED);
                subscription.setUpdatedAt(now);
                subscriptionRepository.save(subscription);
            }
        }
    }
}
