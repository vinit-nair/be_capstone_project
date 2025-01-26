package com.example.gopaywallet.service;


import com.example.gopaywallet.model.PasswordResetToken;
import com.example.gopaywallet.model.User;
import com.example.gopaywallet.repository.PasswordResetTokenRepository;
import com.example.gopaywallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @Transactional
    public String createPasswordResetTokenForUser(User user) {
        // Delete any existing tokens for this user
        tokenRepository.deleteByUser_Id(user.getId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(passwordResetToken);

        return token;
    }

    public void sendPasswordResetEmail(User user, String token) {
        String resetUrl = "your-app-url/reset-password?token=" + token;
        String htmlMessage = String.format("""
            <html>
                <body>
                    <h2>Password Reset Request</h2>
                    <p>Hello %s,</p>
                    <p>You have requested to reset your password. Click the link below to proceed:</p>
                    <p><a href="%s">Reset Password</a></p>
                    <p>This link will expire in 24 hours.</p>
                    <p>If you didn't request this, please ignore this email.</p>
                    <br>
                    <p>Best regards,</p>
                    <p>GoPay Wallet Team</p>
                </body>
            </html>
            """, user.getFullName(), resetUrl);
        
        emailService.sendEmail(user.getEmail(), "Password Reset Request", htmlMessage);
    }

    @Transactional(readOnly = true)
    public boolean validatePasswordResetToken(String token) {
        return tokenRepository.findByToken(token)
            .map(resetToken -> !resetToken.isExpired() && !resetToken.isUsed())
            .orElse(false);
    }

    @Transactional
    public User getUserByPasswordResetToken(String token) {
        return tokenRepository.findByToken(token)
            .map(PasswordResetToken::getUser)
            .orElseThrow(() -> new RuntimeException("Invalid token"));
    }

    @Transactional
    public void invalidateToken(String token) {
        tokenRepository.findByToken(token)
            .ifPresent(resetToken -> {
                resetToken.setUsed(true);
                tokenRepository.save(resetToken);
            });
    }
} 