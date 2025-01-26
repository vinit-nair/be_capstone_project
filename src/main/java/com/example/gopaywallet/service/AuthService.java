package com.example.gopaywallet.service;


import com.example.gopaywallet.exception.AuthenticationException;
import com.example.gopaywallet.exception.ResourceNotFoundException;
import com.example.gopaywallet.exception.UserAlreadyExistsException;
import com.example.gopaywallet.model.*;
import com.example.gopaywallet.repository.UserRepository;
import com.example.gopaywallet.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;
    private final OtpService otpService;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            PasswordResetService passwordResetService,
            OtpService otpService
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
        this.otpService = otpService;
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("EMAIL_NOT_FOUND"));

        String otp = otpService.generateOtp(email);
        emailService.sendOtp(email, otp);
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);
            
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("User not found"));

            return new LoginResponse(jwt, user);
        } catch (Exception e) {
            throw new AuthenticationException("Invalid email or password");
        }
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(savedUser.getEmail());

        return new RegisterResponse(token, savedUser);
    }



    public ForgotPasswordResponse requestPasswordReset(ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("User not found"));

            String token = passwordResetService.createPasswordResetTokenForUser(user);
            
            String resetLink = "http://your-frontend-url/reset-password?token=" + token;
            String emailContent = createPasswordResetEmailContent(user.getFullName(), resetLink);

            emailService.sendEmail(
                user.getEmail(),
                "Password Reset Request",
                emailContent
            );

            return new ForgotPasswordResponse(
                "Password reset instructions sent to your email",
                token
            );
        } catch (AuthenticationException e) {
            // Return success to prevent email enumeration
            return new ForgotPasswordResponse(
                "If an account exists with this email, password reset instructions will be sent",
                ""
            );
        }
    }

    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        if (!passwordResetService.validatePasswordResetToken(request.getToken())) {
            throw new AuthenticationException("Invalid or expired token");
        }

        User user = passwordResetService.getUserByPasswordResetToken(request.getToken());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        passwordResetService.invalidateToken(request.getToken());

        return new ResetPasswordResponse("Password successfully reset", true);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new AuthenticationException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AuthenticationException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private String createPasswordResetEmailContent(String fullName, String resetLink) {
        return String.format(
            "Hello %s,\n\nPlease click the link below to reset your password:\n%s\n\n" +
            "This link will expire in 30 minutes.\n\n" +
            "If you didn't request this, please ignore this email.",
            fullName,
            resetLink
        );
    }
} 