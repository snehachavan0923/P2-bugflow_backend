package com.bugflow.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bugflow.entity.Subscription;
import com.bugflow.entity.SubscriptionStatus;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

    Optional<Subscription> findByOrganizationId(String organizationId);

    Optional<Subscription> findByOwnerId(String ownerId);

    List<Subscription> findByStatusAndEndDateLessThanEqual(SubscriptionStatus status, LocalDateTime endDate);

    long countByStatus(SubscriptionStatus status);
}
