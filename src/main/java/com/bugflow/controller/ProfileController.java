package com.bugflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugflow.dto.UpdatePasswordRequest;
import com.bugflow.entity.User;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new BadRequestException("Unable to determine current user");
        }

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        String newPassword = request.getNewPassword();

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new BadRequestException("New password is required");
        }

        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            throw new BadRequestException("Password must be at least 8 characters and contain an uppercase letter, lowercase letter, number, and special character.");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException("Please enter a new password. The new password cannot be the same as the current password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        return ResponseEntity.ok("Password updated");
    }
}
