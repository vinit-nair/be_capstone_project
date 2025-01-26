package com.example.gopaywallet.service;

import com.example.gopaywallet.model.User;
import com.example.gopaywallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Map<String, PasswordResetToken> tokenStore = new HashMap<>();

    @Autowired
    public PasswordResetService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public String createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(
                token,
                user.getEmail(),
                LocalDateTime.now().plusMinutes(30)
        );
        tokenStore.put(token, passwordResetToken);
        return token;
    }

    public boolean validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenStore.get(token);
        return resetToken != null && !resetToken.isExpired();
    }

    public User getUserByPasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenStore.get(token);
        if (resetToken == null || resetToken.isExpired()) {
            throw new RuntimeException("Invalid or expired token");
        }
        return userRepository.findByEmail(resetToken.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void invalidateToken(String token) {
        tokenStore.remove(token);
    }

    private static class PasswordResetToken {
        private final String token;
        private final String userEmail;
        private final LocalDateTime expiryDate;

        public PasswordResetToken(String token, String userEmail, LocalDateTime expiryDate) {
            this.token = token;
            this.userEmail = userEmail;
            this.expiryDate = expiryDate;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryDate);
        }
    }
}