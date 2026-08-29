package com.bugflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bugflow.entity.Invitation;
import com.bugflow.entity.InvitationStatus;

public interface InvitationRepository extends MongoRepository<Invitation, String> {
    Optional<Invitation> findByToken(String token);

    List<Invitation> findByEmailAndProjectIdAndStatus(String email, String projectId, InvitationStatus status);

    boolean existsByEmailAndProjectIdAndStatus(String email, String projectId, InvitationStatus status);

    List<Invitation> findByProjectIdAndStatus(String projectId, InvitationStatus status);
}
