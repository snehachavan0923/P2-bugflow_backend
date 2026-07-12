package com.bugflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bugflow.entity.Plan;

public interface PlanRepository extends MongoRepository<Plan, String> {
    Optional<Plan> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
   List<Plan> findByIsActiveTrueOrderByDisplayOrderAsc();
}
