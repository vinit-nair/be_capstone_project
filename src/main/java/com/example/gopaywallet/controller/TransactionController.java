package com.example.gopaywallet.controller;

import com.example.gopaywallet.dto.TransactionDTO;
import com.example.gopaywallet.dto.TransactionRequest;
import com.example.gopaywallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @RequestParam Long userId,
            @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(userId, request));
    }

    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getUserTransactions(
            @RequestParam Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(transactionService.getUserTransactions(userId, pageable));
    }
} 