package com.bugflow.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bugflow.entity.Project;

public interface ProjectRepository extends MongoRepository<Project, String> {

}