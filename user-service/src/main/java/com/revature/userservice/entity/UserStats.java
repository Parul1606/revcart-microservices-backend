package com.revature.userservice.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_stats")
public class UserStats {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private Integer totalOrders = 0;

    private Integer completedOrders = 0;

    private Integer cancelledOrders = 0;

    private BigDecimal totalSpent = BigDecimal.ZERO;

    private BigDecimal walletBalance = BigDecimal.ZERO;

    private Integer loyaltyPoints = 0;

    private BigDecimal savedAmount = BigDecimal.ZERO;

    public UserStats() {
    }

    public UserStats(Long id, User user, Integer totalOrders, Integer completedOrders, Integer cancelledOrders,
            BigDecimal totalSpent, BigDecimal walletBalance, Integer loyaltyPoints, BigDecimal savedAmount) {
        this.id = id;
        this.user = user;
        this.totalOrders = totalOrders;
        this.completedOrders = completedOrders;
        this.cancelledOrders = cancelledOrders;
        this.totalSpent = totalSpent;
        this.walletBalance = walletBalance;
        this.loyaltyPoints = loyaltyPoints;
        this.savedAmount = savedAmount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(Integer completedOrders) {
        this.completedOrders = completedOrders;
    }

    public Integer getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(Integer cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public BigDecimal getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(BigDecimal savedAmount) {
        this.savedAmount = savedAmount;
    }

    // Builder
    public static UserStatsBuilder builder() {
        return new UserStatsBuilder();
    }

    public static class UserStatsBuilder {
        private Long id;
        private User user;
        private Integer totalOrders = 0;
        private Integer completedOrders = 0;
        private Integer cancelledOrders = 0;
        private BigDecimal totalSpent = BigDecimal.ZERO;
        private BigDecimal walletBalance = BigDecimal.ZERO;
        private Integer loyaltyPoints = 0;
        private BigDecimal savedAmount = BigDecimal.ZERO;

        UserStatsBuilder() {
        }

        public UserStatsBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserStatsBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserStatsBuilder totalOrders(Integer totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }

        public UserStatsBuilder completedOrders(Integer completedOrders) {
            this.completedOrders = completedOrders;
            return this;
        }

        public UserStatsBuilder cancelledOrders(Integer cancelledOrders) {
            this.cancelledOrders = cancelledOrders;
            return this;
        }

        public UserStatsBuilder totalSpent(BigDecimal totalSpent) {
            this.totalSpent = totalSpent;
            return this;
        }

        public UserStatsBuilder walletBalance(BigDecimal walletBalance) {
            this.walletBalance = walletBalance;
            return this;
        }

        public UserStatsBuilder loyaltyPoints(Integer loyaltyPoints) {
            this.loyaltyPoints = loyaltyPoints;
            return this;
        }

        public UserStatsBuilder savedAmount(BigDecimal savedAmount) {
            this.savedAmount = savedAmount;
            return this;
        }

        public UserStats build() {
            return new UserStats(id, user, totalOrders, completedOrders, cancelledOrders, totalSpent, walletBalance,
                    loyaltyPoints, savedAmount);
        }
    }
}
