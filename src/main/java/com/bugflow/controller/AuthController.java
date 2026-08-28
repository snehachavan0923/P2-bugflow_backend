package com.bugflow.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugflow.dto.LoginRequest;
import com.bugflow.dto.SignupRequest;
import com.bugflow.entity.User;
import com.bugflow.service.AuthService;


@RestController
@RequestMapping("/auth")
public class AuthController {
 
    @Autowired
    private AuthService authService;

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
}