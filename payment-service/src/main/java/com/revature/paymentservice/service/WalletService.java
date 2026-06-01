package com.revature.paymentservice.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.revature.paymentservice.entity.Wallet;
import com.revature.paymentservice.entity.WalletTransaction;
import com.revature.paymentservice.repository.WalletRepository;
import com.revature.paymentservice.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    public WalletService(WalletRepository walletRepository, WalletTransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUserId(userId);
                    wallet.setBalance(BigDecimal.ZERO);
                    return walletRepository.save(wallet);
                });
    }

    public Wallet addMoney(Long userId, BigDecimal amount, String description, String referenceId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        Wallet wallet = getOrCreateWallet(userId);
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal newBalance = balanceBefore.add(amount);
        wallet.setBalance(newBalance);

        Wallet savedWallet = walletRepository.save(wallet);

        // Create transaction record
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(wallet.getId());
        transaction.setUserId(userId);
        transaction.setType("CREDIT");
        transaction.setAmount(amount);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(description != null ? description : "Money added to wallet");
        transaction.setReferenceId(referenceId);
        transactionRepository.save(transaction);

        return savedWallet;
    }

    public Wallet deductMoney(Long userId, BigDecimal amount, String description, String referenceId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        Wallet wallet = getOrCreateWallet(userId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal newBalance = balanceBefore.subtract(amount);
        wallet.setBalance(newBalance);

        Wallet savedWallet = walletRepository.save(wallet);

        // Create transaction record
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(wallet.getId());
        transaction.setUserId(userId);
        transaction.setType("DEBIT");
        transaction.setAmount(amount);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(description != null ? description : "Money deducted from wallet");
        transaction.setReferenceId(referenceId);
        transactionRepository.save(transaction);

        return savedWallet;
    }

    public Wallet getWalletByUserId(Long userId) {
        return getOrCreateWallet(userId);
    }

    public BigDecimal getBalance(Long userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return wallet.getBalance();
    }

    public List<WalletTransaction> getTransactionHistory(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Map<String, Object> createTopupOrder(Long userId, BigDecimal amount) {
        try {
            // Create actual Razorpay order
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            org.json.JSONObject orderRequest = new org.json.JSONObject();
            orderRequest.put("amount", amount.multiply(new BigDecimal("100")).intValue()); // Convert to paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "wallet_" + userId + "_" + System.currentTimeMillis());

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            Map<String, Object> orderDetails = new java.util.HashMap<>();
            orderDetails.put("razorpayOrderId", razorpayOrder.get("id"));
            orderDetails.put("amount", amount); // Store original amount in rupees
            orderDetails.put("currency", "INR");
            orderDetails.put("key", razorpayKeyId);

            return orderDetails;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }

    public WalletTransaction verifyTopup(Long userId, String razorpayOrderId,
            String razorpayPaymentId, String razorpaySignature) {
        try {
            // Fetch order details from Razorpay to get the actual amount
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            Order order = razorpayClient.orders.fetch(razorpayOrderId);

            // Get amount in paise and convert to rupees
            int amountInPaise = order.get("amount");
            BigDecimal amount = new BigDecimal(amountInPaise).divide(new BigDecimal("100"));

            // Credit wallet
            Wallet wallet = addMoney(userId, amount, "Wallet topup via Razorpay", razorpayPaymentId);

            // Return the latest transaction
            return transactionRepository.findByUserId(userId).get(0);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify topup: " + e.getMessage(), e);
        }
    }
}
