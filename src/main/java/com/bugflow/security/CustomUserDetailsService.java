package com.bugflow.security;

import com.bugflow.entity.Organization;
import com.bugflow.entity.OrganizationStatus;
import com.bugflow.entity.User;
import com.bugflow.exception.OrganizationSuspendedException;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        if (user.getOrganizationId() != null) {

            Organization organization =
                    organizationRepository
                    .findById(user.getOrganizationId())
                    .orElse(null);

            if (organization != null &&
                organization.getStatus() == OrganizationStatus.SUSPENDED) {

                throw new OrganizationSuspendedException(
                        "Your organization has been suspended. Please contact BugFlow support.");
            }

        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}