package com.revature.deliveryservice.service;

import com.revature.deliveryservice.entity.Otp;
import com.revature.deliveryservice.repository.OtpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    private final OtpRepository otpRepository;
    private final RestTemplate restTemplate;

    @Value("${sms.test.mode:true}")
    private boolean testMode;

    @Value("${fast2sms.api.key:dummy}")
    private String fast2smsApiKey;

    public OtpService(OtpRepository otpRepository, RestTemplate restTemplate) {
        this.otpRepository = otpRepository;
        this.restTemplate = restTemplate;
    }

    public String generateAndSaveOtp(String phone, String type) {
        String otpCode = generateOtpCode();

        Otp otp = new Otp(phone, otpCode, type);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otpRepository.save(otp);

        if (testMode) {
            log.info("TEST MODE: OTP for {}: {}", phone, otpCode);
        } else {
            sendSms(phone, otpCode);
        }

        return otpCode;
    }

    @Transactional
    public boolean verifyOtp(String phone, String otpCode) {
        Optional<Otp> otpOpt = otpRepository
                .findTopByPhoneAndTypeAndUsedFalseOrderByCreatedAtDesc(phone, "DELIVERY_LOGIN");

        if (otpOpt.isEmpty()) {
            log.warn("No OTP found for phone: {}", phone);
            return false;
        }

        Otp otp = otpOpt.get();

        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            log.warn("OTP expired for phone: {}", phone);
            return false;
        }

        if (!otp.getOtpCode().equals(otpCode)) {
            log.warn("Invalid OTP for phone: {}", phone);
            return false;
        }

        otp.setUsed(true);
        otpRepository.save(otp);

        log.info("OTP verified successfully for phone: {}", phone);
        return true;
    }

    private String generateOtpCode() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendSms(String phone, String otpCode) {
        try {
            String message = "Your OTP for delivery partner login is: " + otpCode;
            log.info("Sending SMS to {}: {}", phone, message);
            // Fast2SMS integration would go here
        } catch (Exception e) {
            log.error("Failed to send SMS: {}", e.getMessage());
        }
    }
}
