package com.example.gopaywallet.controller;


import com.example.gopaywallet.dto.RewardsDTO;
import com.example.gopaywallet.service.RewardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RewardsController {

    private final RewardsService rewardsService;

    @GetMapping
    public ResponseEntity<RewardsDTO> getUserRewards(
            @RequestParam Long userId) {
        return ResponseEntity.ok(rewardsService.getRewards(userId));
    }
}
