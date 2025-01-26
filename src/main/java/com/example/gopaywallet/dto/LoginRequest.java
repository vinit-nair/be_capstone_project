package com.example.gopaywallet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request payload")
@Data
public class LoginRequest {
    @Schema(description = "User email", example = "user@example.com")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "User password", example = "password123")
    @NotBlank(message = "Password is required")
    private String password;
} 