package com.example.gopaywallet.repository;

import com.example.gopaywallet.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUserIdOrderByDateTimeDesc(Long userId, Pageable pageable);
    List<Transaction> findByUserId(Long userId);
} 