package com.bugflow.service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bugflow.dto.ForgotPasswordRequest;
import com.bugflow.dto.ResetPasswordRequest;
import com.bugflow.entity.PasswordResetToken;
import com.bugflow.entity.User;
import com.bugflow.exception.BadRequestException;
import com.bugflow.repository.PasswordResetTokenRepository;
import com.bugflow.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {

    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_EXPIRATION_MINUTES = 30;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            MailService mailService) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public String requestPasswordReset(ForgotPasswordRequest request) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim();

        if (email.isBlank()) {
            throw new BadRequestException("Email is required");
        }

        Optional<User> maybeUser = userRepository.findByEmail(email);

        if (maybeUser.isEmpty()) {
            return "If an account exists for this email, password reset instructions have been sent.";
        }

        User user = maybeUser.get();

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return "If an account exists for this email, password reset instructions have been sent.";
        }

        passwordResetTokenRepository.deleteByUserId(user.getId());

        String rawToken = generateSecureToken();
        String tokenHash = hashToken(rawToken);
        PasswordResetToken token = new PasswordResetToken();
        token.setTokenHash(tokenHash);
        token.setUserId(user.getId());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES));
        passwordResetTokenRepository.save(token);

        String resetUrl = String.format("%s/reset-password?token=%s", frontendBaseUrl, rawToken);
        mailService.sendPasswordResetEmail(user.getEmail(), resetUrl, TOKEN_EXPIRATION_MINUTES);

        return "If an account exists for this email, password reset instructions have been sent.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        if (request == null || request.getToken() == null || request.getToken().isBlank()) {
            throw new BadRequestException("Reset token is required");
        }

        String token = request.getToken().trim();
        String tokenHash = hashToken(token);

        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadRequestException("Reset link is invalid or expired"));

        if (resetToken.getUsedAt() != null) {
            throw new BadRequestException("This reset link has already been used");
        }

        if (resetToken.getExpiresAt() == null || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new BadRequestException("Reset link is invalid or expired");
        }

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new BadRequestException("New password is required");
        }

        if (!request.getNewPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            throw new BadRequestException("Password must be at least 8 characters and contain an uppercase letter, lowercase letter, number, and special character.");
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Please enter a new password. The new password cannot be the same as your current password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);

        return "Password reset successfully";
    }

    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[TOKEN_BYTES];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash password reset token", ex);
        }
    }
}
