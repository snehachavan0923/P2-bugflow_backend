package com.bugflow.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.bugflow.entity.Project;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {

    //  ONLY return projects of logged-in user
    List<Project> findByOwnerId(String ownerId);
    
    List<Project> findByOrganizationId(
            String organizationId);
    
    long countByOrganizationId(
            String organizationId);
} 