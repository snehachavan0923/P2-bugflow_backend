package com.bugflow.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bugflow.entity.Issue;

public interface IssueRepository
extends MongoRepository<Issue, String> {

List<Issue> findByProjectId(String projectId);

List<Issue> findByAssignedToEmail(String email);

List<Issue> findByStatus(String status);

List<Issue> findByAssignedToRole(String role);
List<Issue> findByProjectIdIn(
        List<String> projectIds);

List<Issue> findByAssignedToEmailAndStatus(
    String email,
    String status);

List<Issue> findByProjectIdAndAssignedToEmail(
    String projectId,
    String email);

List<Issue> findByProjectIdAndAssignedToEmailAndStatus(
    String projectId, 
    String email,
    String status);

List<Issue> findByProjectIdAndDeveloperEmail(
        String projectId,
        String developerEmail);

List<Issue> findByProjectIdAndAssignedToRole(
        String projectId,
        String role); 

long countByProjectIdInAndAssignedToUserIdIsNotNull(
        List<String> projectIds);

long countByProjectIdInAndStatus(
        List<String> projectIds,
        String status);

long countByProjectIdIn(
        List<String> projectIds);

long countByAssignedToEmail(
        String email);

long countByAssignedToEmailAndStatus(
        String email,
        String status);

long countByAssignedToRole(
        String role);
}


