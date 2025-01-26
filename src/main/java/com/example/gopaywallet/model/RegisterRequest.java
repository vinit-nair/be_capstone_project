package com.example.gopaywallet.model;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
} 