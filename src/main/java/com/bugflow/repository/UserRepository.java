package com.bugflow.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.bugflow.entity.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    List<User> findByOrganizationId(
            String organizationId);
    
    long countByOrganizationId(
            String organizationId);
}
 