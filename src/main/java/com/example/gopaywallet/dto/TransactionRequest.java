package com.example.gopaywallet.dto;

import com.example.gopaywallet.model.TransactionType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String title;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private String recipientName;
    private String recipientPhone;
} 