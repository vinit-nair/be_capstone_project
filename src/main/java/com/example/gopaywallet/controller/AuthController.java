package com.example.gopaywallet.controller;

import com.example.gopaywallet.exception.AuthenticationException;
import com.example.gopaywallet.exception.UserAlreadyExistsException;
import com.example.gopaywallet.model.*;
import com.example.gopaywallet.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("Failed to register user: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (AuthenticationException e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            return ResponseEntity.ok(authService.requestPasswordReset(request));
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(new ForgotPasswordResponse("Failed to process password reset request", ""));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            return ResponseEntity.ok(authService.resetPassword(request));
        } catch (AuthenticationException e) {
            return ResponseEntity
                .badRequest()
                .body(new ResetPasswordResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            authService.changePassword(email, request);
            return ResponseEntity.ok(new SuccessResponse("Password successfully changed"));
        } catch (AuthenticationException e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
} 