package com.example.gopaywallet.model;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
} 