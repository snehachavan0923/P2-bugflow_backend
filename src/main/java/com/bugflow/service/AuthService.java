package com.bugflow.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bugflow.entity.User;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.entity.Role;
import com.bugflow.repository.UserRepository;
import com.bugflow.dto.LoginRequest;
import com.bugflow.dto.SignupRequest;
import com.bugflow.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public User signup(SignupRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        //  FIXED
        user.setRole(Role.Owner); 

        return userRepository.save(user);
    }

        public Map<String, Object> login(LoginRequest request) {

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found"));

            if (!passwordEncoder.matches(
                    request.getPassword(),
                    user.getPassword())) { 

                throw new BadRequestException("Invalid password");
            }

            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getRole().name()
            );

            Map<String, Object> response = new HashMap<>();

            response.put("token", token);

            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole()); 
            response.put(
            		"organizationId",
            		user.getOrganizationId());

            return response;
        }
}