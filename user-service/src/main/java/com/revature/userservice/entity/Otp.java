package com.revature.userservice.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String otpCode;

    @Column(nullable = false)
    private String type; // LOGIN, REGISTER, RESET

    private boolean used = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    public Otp() {
    }

    public Otp(Long id, String mobile, String otpCode, String type, boolean used, LocalDateTime createdAt,
            LocalDateTime expiresAt) {
        this.id = id;
        this.mobile = mobile;
        this.otpCode = otpCode;
        this.type = type;
        this.used = used;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    // Builder
    public static OtpBuilder builder() {
        return new OtpBuilder();
    }

    public static class OtpBuilder {
        private Long id;
        private String mobile;
        private String otpCode;
        private String type;
        private boolean used = false;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;

        OtpBuilder() {
        }

        public OtpBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public OtpBuilder mobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public OtpBuilder otpCode(String otpCode) {
            this.otpCode = otpCode;
            return this;
        }

        public OtpBuilder type(String type) {
            this.type = type;
            return this;
        }

        public OtpBuilder used(boolean used) {
            this.used = used;
            return this;
        }

        public OtpBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OtpBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Otp build() {
            return new Otp(id, mobile, otpCode, type, used, createdAt, expiresAt);
        }
    }
}
