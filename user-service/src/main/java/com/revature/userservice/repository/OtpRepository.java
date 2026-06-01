package com.revature.userservice.repository;

import com.revature.userservice.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByMobileAndOtpCodeAndUsedFalse(String mobile, String otpCode);

    Optional<Otp> findTopByMobileAndTypeOrderByCreatedAtDesc(String mobile, String type);
}
