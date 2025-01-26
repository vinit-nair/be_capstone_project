package com.example.gopaywallet.controller;

import com.example.gopaywallet.dto.TransactionDTO;
import com.example.gopaywallet.dto.TransactionRequest;
import com.example.gopaywallet.service.TransactionService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Transaction management APIs")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(
            summary = "Get user transactions",
            description = "Retrieve paginated list of user transactions"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved transactions",
                    content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access"
            )
    })
    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getUserTransactions(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(transactionService.getUserTransactions(userId, Pageable.ofSize(size).withPage(page)));
    }

    @Operation(
            summary = "Create new transaction",
            description = "Create a new transaction for the user"
    )
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @RequestBody @Valid TransactionRequest request
    ) {
        return ResponseEntity.ok(transactionService.createTransaction(userId, request));
    }
} 