package com.bugflow.controller;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bugflow.dto.UpdatePasswordRequest;
import com.bugflow.entity.User;
import com.bugflow.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileController profileController;

    @Test
    void changePasswordShouldUseAuthenticatedUserIdentity() {
        String email = "dev@example.com";
        String currentPassword = "OldPass123!";
        String newPassword = "NewPass123!";

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword(currentPassword);
        request.setNewPassword(newPassword);

        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded-current-password");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, "unused");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, "encoded-current-password")).thenReturn(true);
        when(passwordEncoder.matches(newPassword, "encoded-current-password")).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded-new-password");

        ResponseEntity<String> response = profileController.changePassword(request, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Password updated", response.getBody());
        assertEquals("encoded-new-password", user.getPassword());
        verify(userRepository).save(user);
    }
}
