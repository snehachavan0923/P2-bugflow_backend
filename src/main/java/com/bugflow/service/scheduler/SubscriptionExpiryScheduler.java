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
    
    // Runs every day at 12:00 AM (midnight) to automatically expire subscriptions
    @Scheduled(cron = "0 0 0 * * ?")
    public void expireSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        List<Subscription> subscriptions = subscriptionRepository
                .findByStatusAndEndDateLessThanEqual(SubscriptionStatus.ACTIVE, now);

        for (Subscription subscription : subscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscription.setUpdatedAt(now);
            subscriptionRepository.save(subscription);
        }
    }
}
