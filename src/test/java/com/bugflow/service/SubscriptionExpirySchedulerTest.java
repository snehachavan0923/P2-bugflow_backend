package com.bugflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bugflow.entity.Subscription;
import com.bugflow.entity.SubscriptionStatus;
import com.bugflow.repository.SubscriptionRepository;
import com.bugflow.service.scheduler.SubscriptionExpiryScheduler;

@ExtendWith(MockitoExtension.class)
class SubscriptionExpirySchedulerTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Test
    void expireSubscriptionsShouldMarkPastActiveSubscriptionsExpired() {
        Subscription expired = new Subscription();
        expired.setStatus(SubscriptionStatus.ACTIVE);
        expired.setEndDate(LocalDateTime.now().minusDays(1));

        Subscription active = new Subscription();
        active.setStatus(SubscriptionStatus.ACTIVE);
        active.setEndDate(LocalDateTime.now().plusDays(1));

        when(subscriptionRepository.findByStatusAndEndDateLessThanEqual(
                org.mockito.ArgumentMatchers.eq(SubscriptionStatus.ACTIVE),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(expired));

        SubscriptionExpiryScheduler scheduler = new SubscriptionExpiryScheduler(subscriptionRepository);
        scheduler.expireSubscriptions();

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(captor.capture());
        assertEquals(SubscriptionStatus.EXPIRED, captor.getValue().getStatus());
    }

    @Test
    void expireSubscriptionsShouldNotSaveWhenNothingExpired() {
        Subscription active = new Subscription();
        active.setStatus(SubscriptionStatus.ACTIVE);
        active.setEndDate(LocalDateTime.now().plusDays(1));

        when(subscriptionRepository.findByStatusAndEndDateLessThanEqual(
                org.mockito.ArgumentMatchers.eq(SubscriptionStatus.ACTIVE),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of());

        SubscriptionExpiryScheduler scheduler = new SubscriptionExpiryScheduler(subscriptionRepository);
        scheduler.expireSubscriptions();

        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }
}
