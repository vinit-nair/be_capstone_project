package com.example.gopaywallet.model;

import jakarta.persistence.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User entity")
@Entity
@Table(name = "users")
@Data
public class User {
    @Schema(description = "Unique identifier")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "User's full name")
    private String fullName;

    @Schema(description = "User's email address")
    @Column(unique = true)
    private String email;

    @Schema(description = "User's phone number")
    @Column(unique = true)
    private String phoneNumber;

    @Schema(description = "Encrypted password")
    private String password;
}