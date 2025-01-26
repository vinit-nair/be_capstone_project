package com.example.gopaywallet.dto;

import com.example.gopaywallet.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transaction request payload")
@Data
public class TransactionRequest {
    @Schema(description = "Transaction title", example = "Payment to John")
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Transaction amount", example = "100.00")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Schema(description = "Transaction type", example = "SEND")
    @NotNull(message = "Type is required")
    private TransactionType type;

    @Schema(description = "Transaction description", example = "Monthly rent payment")
    private String description;

    @Schema(description = "Recipient name", example = "John Doe")
    private String recipientName;

    @Schema(description = "Recipient phone number", example = "+1234567890")
    private String recipientPhone;
} 