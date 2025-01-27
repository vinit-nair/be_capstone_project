package com.example.gopaywallet.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RewardsDTO {
    private BigDecimal points;
    private List<String> rewardsList;
}
