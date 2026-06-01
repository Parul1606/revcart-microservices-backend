package com.revature.paymentservice.controller;

import com.revature.paymentservice.entity.Wallet;
import com.revature.paymentservice.entity.WalletTransaction;
import com.revature.paymentservice.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long userId) {
        Wallet wallet = walletService.getOrCreateWallet(userId);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<Map<String, BigDecimal>> getBalance(@PathVariable Long userId) {
        BigDecimal balance = walletService.getBalance(userId);

        Map<String, BigDecimal> response = new HashMap<>();
        response.put("balance", balance);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-money")
    public ResponseEntity<Map<String, Object>> addMoney(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = (String) request.get("description");
            String referenceId = (String) request.get("referenceId");

            Wallet wallet = walletService.addMoney(userId, amount, description, referenceId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Money added successfully");
            response.put("balance", wallet.getBalance());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/deduct-money")
    public ResponseEntity<Map<String, Object>> deductMoney(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = (String) request.get("description");
            String referenceId = (String) request.get("referenceId");

            Wallet wallet = walletService.deductMoney(userId, amount, description, referenceId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Money deducted successfully");
            response.put("balance", wallet.getBalance());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions(@PathVariable Long userId) {
        List<WalletTransaction> transactions = walletService.getTransactionHistory(userId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/topup/create-order")
    public ResponseEntity<Map<String, Object>> createWalletTopupOrder(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @RequestBody Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());

            Map<String, Object> orderDetails = walletService.createTopupOrder(userId, amount);

            return ResponseEntity.ok(orderDetails);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to create payment order: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getBalanceForCurrentUser(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        BigDecimal balance = walletService.getBalance(userId);

        Map<String, BigDecimal> response = new HashMap<>();
        response.put("balance", balance);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactionsForCurrentUser(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        List<WalletTransaction> transactions = walletService.getTransactionHistory(userId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/topup/verify")
    public ResponseEntity<Map<String, Object>> verifyWalletTopup(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @RequestBody Map<String, String> request) {
        try {
            String razorpayOrderId = request.get("razorpayOrderId");
            String razorpayPaymentId = request.get("razorpayPaymentId");
            String razorpaySignature = request.get("razorpaySignature");

            WalletTransaction transaction = walletService.verifyTopup(
                    userId, razorpayOrderId, razorpayPaymentId, razorpaySignature);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Wallet credited successfully");
            response.put("transaction", transaction);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Payment verification failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
