package com.example.gopaywallet.service;

import com.example.gopaywallet.dto.RewardsDTO;
import com.example.gopaywallet.model.Transaction;
import com.example.gopaywallet.model.TransactionType;
import com.example.gopaywallet.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardsService {

    private final TransactionRepository transactionRepository;
    private static final List<String> rewardsList = List.of(
            "5% on amount received",
            "10% on amount sent"
    );

    public RewardsDTO getRewards(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        BigDecimal rPoints = transactions.stream()
                .filter(i -> TransactionType.RECEIVE.equals(i.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(5), RoundingMode.UNNECESSARY);
        BigDecimal sPoints = transactions.stream()
                .filter(i -> TransactionType.SEND.equals(i.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(10), RoundingMode.UNNECESSARY);

        RewardsDTO rewards = new RewardsDTO();
        rewards.setRewardsList(rewardsList);
        rewards.setPoints(rPoints.add(sPoints));
        return rewards;
    }
}
