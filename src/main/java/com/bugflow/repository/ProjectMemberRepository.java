package com.bugflow.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bugflow.entity.ProjectMember;

public interface ProjectMemberRepository extends MongoRepository<ProjectMember, String> {

    List<ProjectMember> findByProjectId(String projectId);
}