package com.bugflow.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bugflow.entity.Organization;

public interface OrganizationRepository
        extends MongoRepository<Organization, String> {

    Optional<Organization>
    findByOwnerId(String ownerId);
    
}