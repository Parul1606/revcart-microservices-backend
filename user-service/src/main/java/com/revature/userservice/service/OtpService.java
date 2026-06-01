package com.revature.userservice.service;

import com.revature.userservice.entity.Otp;
import com.revature.userservice.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final RestTemplate restTemplate;

    @Value("${fast2sms.api.key:}")
    private String fast2smsApiKey;

    @Value("${sms.test.mode:true}")
    private boolean testMode;

    private static final int OTP_EXPIRY_MINUTES = 5;

    public OtpService(OtpRepository otpRepository, RestTemplate restTemplate) {
        this.otpRepository = otpRepository;
        this.restTemplate = restTemplate;
    }

    public String generateAndSaveOtp(String mobile, String type) {
        // Generate 6-digit OTP
        Random random = new Random();
        String otpCode = String.format("%06d", random.nextInt(1_000_000));

        // Save to database
        Otp otp = Otp.builder()
                .mobile(mobile)
                .otpCode(otpCode)
                .type(type)
                .used(false)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();
        otpRepository.save(otp);

        // Send SMS if not in test mode
        if (!testMode && fast2smsApiKey != null && !fast2smsApiKey.isBlank()) {
            boolean sent = sendViaFast2Sms(mobile, otpCode);
            if (!sent) {
                System.err.println("Failed to send OTP SMS to " + mobile);
            }
        } else {
            // Test mode - log the OTP (remove in production)
            System.out.println("TEST MODE: OTP for " + mobile + " = " + otpCode);
        }

        return otpCode;
    }

    private boolean sendViaFast2Sms(String mobile, String otpCode) {
        try {
            String url = "https://www.fast2sms.com/dev/bulkV2";
            String message = otpCode + " is your OTP for RevCart. Valid for " + OTP_EXPIRY_MINUTES + " minutes.";

            Map<String, Object> payload = new HashMap<>();
            payload.put("route", "otp");
            payload.put("message", message);
            payload.put("language", "english");
            payload.put("flash", 0);
            payload.put("numbers", mobile);

            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", fast2smsApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().contains("\"return\":true");
            } else {
                System.err.println("Fast2SMS non-2xx: " + response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Exception sending OTP via Fast2SMS: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyOtp(String mobile, String otpCode) {
        return otpRepository.findByMobileAndOtpCodeAndUsedFalse(mobile, otpCode)
                .map(otp -> {
                    if (otp.getExpiresAt() == null || otp.getExpiresAt().isBefore(LocalDateTime.now())) {
                        System.out.println("OTP expired for " + mobile);
                        return false;
                    }
                    otp.setUsed(true);
                    otpRepository.save(otp);
                    return true;
                })
                .orElse(false);
    }
}
