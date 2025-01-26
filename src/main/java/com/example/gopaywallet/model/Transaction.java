package com.example.gopaywallet.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private BigDecimal amount;
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(length = 500)
    private String description;

    private String recipientName;
    private String recipientPhone;
}