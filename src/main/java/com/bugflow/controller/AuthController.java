package com.bugflow.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugflow.dto.ForgotPasswordRequest;
import com.bugflow.dto.LoginRequest;
import com.bugflow.dto.ResetPasswordRequest;
import com.bugflow.dto.SignupRequest;
import com.bugflow.entity.User;
import com.bugflow.service.AuthService;
import com.bugflow.service.PasswordResetService;


@RestController
@RequestMapping("/auth")
public class AuthController {
 
    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {

        Map<String, Object> response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth0")
    public ResponseEntity<Map<String, Object>> auth0Login(
            @RequestBody com.bugflow.dto.Auth0LoginRequest request) {

        Map<String, Object> response =
                authService.loginWithAuth0(request.getAccessToken());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String message = passwordResetService.requestPasswordReset(request);
        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        String message = passwordResetService.resetPassword(request);
        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}