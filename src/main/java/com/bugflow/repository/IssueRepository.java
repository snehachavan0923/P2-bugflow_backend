package com.bugflow.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bugflow.entity.Issue;

public interface IssueRepository extends MongoRepository<Issue, String> {

    List<Issue> findByProjectId(String projectId);
}