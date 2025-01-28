package com.example.gopaywallet.controller;

import com.example.gopaywallet.dto.UserDTO;
import com.example.gopaywallet.service.UserService;
import com.example.gopaywallet.exception.*;
import com.example.gopaywallet.model.*;
import com.example.gopaywallet.service.AuthService;
import com.example.gopaywallet.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;
    private final UserService userService;

    @Operation(
            summary = "User registration",
            description = "Register new user with email, password, and other details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
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

    @Operation(
            summary = "User login",
            description = "Authenticate user with email and password to receive JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(new com.example.gopaywallet.model.ApiResponse(true, "Password reset successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new com.example.gopaywallet.model.ApiResponse(false, e.getMessage()));
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok(new com.example.gopaywallet.model.ApiResponse(true, "OTP sent to your email"));
        } catch (ResourceNotFoundException e) {
            // Return 400 Bad Request instead of 404 Not Found
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.example.gopaywallet.model.ApiResponse(false, "Email not registered"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new com.example.gopaywallet.model.ApiResponse(false, "Failed to process request"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        try {
            otpService.validateOtp(request.getEmail(), request.getOtp());
            return ResponseEntity.ok(new com.example.gopaywallet.model.ApiResponse(true, "OTP verified successfully"));
        } catch (InvalidOtpException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.example.gopaywallet.model.ApiResponse(false, "Invalid OTP"));
        } catch (OtpExpiredException e) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(new com.example.gopaywallet.model.ApiResponse(false, "OTP has expired"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new com.example.gopaywallet.model.ApiResponse(false, "Failed to verify OTP"));
        }
    }

    @Operation(
            summary = "Get user details",
            description = "Fetch details of the logged-in user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("user/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.updateUser(id, updatedUser));
    }
} 