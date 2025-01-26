package com.example.gopaywallet.service;

import com.example.gopaywallet.dto.TransactionDTO;
import com.example.gopaywallet.dto.TransactionRequest;
import com.example.gopaywallet.model.Transaction;
import com.example.gopaywallet.model.User;
import com.example.gopaywallet.repository.TransactionRepository;
import com.example.gopaywallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionDTO createTransaction(Long userId, TransactionRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTitle(request.getTitle());
        transaction.setAmount(request.getAmount());
        transaction.setDateTime(LocalDateTime.now());
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription());
        transaction.setRecipientName(request.getRecipientName());
        transaction.setRecipientPhone(request.getRecipientPhone());

        transaction = transactionRepository.save(transaction);
        return TransactionDTO.fromTransaction(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionDTO> getUserTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByDateTimeDesc(userId, pageable)
            .map(TransactionDTO::fromTransaction);
    }
} 