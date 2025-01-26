package com.example.gopaywallet.dto;

import com.example.gopaywallet.model.Transaction;
import com.example.gopaywallet.model.TransactionType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long id;
    private String title;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private TransactionType type;
    private String description;
    private String recipientName;
    private String recipientPhone;

    public static TransactionDTO fromTransaction(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setTitle(transaction.getTitle());
        dto.setAmount(transaction.getAmount());
        dto.setDateTime(transaction.getDateTime());
        dto.setType(transaction.getType());
        dto.setDescription(transaction.getDescription());
        dto.setRecipientName(transaction.getRecipientName());
        dto.setRecipientPhone(transaction.getRecipientPhone());
        return dto;
    }
} 