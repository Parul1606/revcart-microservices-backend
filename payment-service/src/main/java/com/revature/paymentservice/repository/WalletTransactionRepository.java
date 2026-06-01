package com.revature.paymentservice.repository;

import com.revature.paymentservice.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByUserId(Long userId);

    List<WalletTransaction> findByWalletId(Long walletId);
}
