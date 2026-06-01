package com.revature.deliveryservice.controller;

import com.revature.deliveryservice.entity.DeliveryPartner;
import com.revature.deliveryservice.repository.DeliveryPartnerRepository;
import com.revature.deliveryservice.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery/auth")
public class DeliveryAuthController {

    private static final Logger log = LoggerFactory.getLogger(DeliveryAuthController.class);

    private final OtpService otpService;
    private final DeliveryPartnerRepository deliveryPartnerRepository;

    @Value("${sms.test.mode:true}")
    private boolean testMode;

    public DeliveryAuthController(OtpService otpService, DeliveryPartnerRepository deliveryPartnerRepository) {
        this.otpService = otpService;
        this.deliveryPartnerRepository = deliveryPartnerRepository;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");

        if (phone == null || phone.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Phone number is required"));
        }

        log.info("Sending OTP to delivery partner: {}", phone);

        try {
            DeliveryPartner partner = deliveryPartnerRepository.findByPhone(phone)
                    .orElse(null);

            if (partner == null) {
                log.warn("Delivery partner not found for phone: {}", phone);
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", "Phone number not registered",
                                "message", "No delivery partner found with this phone number. Please register first."));
            }

            String otpCode = otpService.generateAndSaveOtp(phone, "DELIVERY_LOGIN");

            log.info("OTP generated for delivery partner: {}", phone);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP sent successfully to " + phone);
            response.put("otpSent", true);
            if (testMode) {
                response.put("debugOtp", otpCode);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error sending OTP to {}: {}", phone, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to send OTP", "message", e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String otp = request.get("otp");

        if (phone == null || phone.isBlank() || otp == null || otp.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Phone and OTP are required"));
        }

        log.info("Verifying OTP for delivery partner: {}", phone);

        try {
            boolean isValid = otpService.verifyOtp(phone, otp);

            if (!isValid) {
                log.warn("Invalid OTP for phone: {}", phone);
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid or expired OTP"));
            }

            DeliveryPartner partner = deliveryPartnerRepository.findByPhone(phone)
                    .orElseThrow(() -> new RuntimeException("Delivery partner not found"));

            // Simple token generation (in production, use proper JWT)
            String token = "delivery_token_" + partner.getId() + "_" + System.currentTimeMillis();

            Map<String, Object> partnerData = new HashMap<>();
            partnerData.put("id", partner.getId());
            partnerData.put("firstName", partner.getFirstName());
            partnerData.put("lastName", partner.getLastName());
            partnerData.put("email", partner.getEmail());
            partnerData.put("phone", partner.getPhone());
            partnerData.put("status", partner.getStatus());
            partnerData.put("rating", partner.getRating());
            partnerData.put("totalDeliveries", partner.getTotalDeliveries());
            partnerData.put("isAvailable", partner.getIsAvailable());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("deliveryPartner", partnerData);

            log.info("Delivery partner logged in successfully: {} (status: {})", phone, partner.getStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error verifying OTP for {}: {}", phone, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Login failed", "message", e.getMessage()));
        }
    }
}
