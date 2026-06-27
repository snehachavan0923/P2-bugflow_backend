package com.bugflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bugflow.entity.ProjectMember;

public interface ProjectMemberRepository
        extends MongoRepository<ProjectMember, String> {

    List<ProjectMember> findByProjectId(String projectId);

    List<ProjectMember> findByEmail(String email);
    List<ProjectMember> findByUserId(String userId);
    
    Optional<ProjectMember>
    findByProjectIdAndUserId(
            String projectId,
            String userId);
    long countByEmail(String email);
    
    boolean existsByUserId(
            String userId);
  
    boolean existsByProjectIdAndEmail(
            String projectId,
            String email);

    void deleteByProjectIdAndUserId( 
            String projectId,
            String userId);
    
    long countByOrganizationId(
            String organizationId);
    long countByProjectId(String projectId);
  
}