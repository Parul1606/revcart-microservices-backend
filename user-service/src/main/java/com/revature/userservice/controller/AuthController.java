package com.revature.userservice.controller;

import com.revature.common.util.JwtUtil;
import com.revature.userservice.entity.User;
import com.revature.userservice.repository.UserRepository;
import com.revature.userservice.service.OtpService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(OtpService otpService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.otpService = otpService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> request) {
        String mobile = request.get("mobile");

        if (mobile == null || mobile.length() != 10) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid mobile number. Must be 10 digits.");
            return ResponseEntity.badRequest().body(error);
        }

        // Check if user exists to determine OTP type
        boolean userExists = userRepository.existsByMobile(mobile);
        String otpType = userExists ? "LOGIN" : "REGISTER";

        // Generate and send OTP
        String otpCode = otpService.generateAndSaveOtp(mobile, otpType);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "OTP sent successfully");
        response.put("otpType", otpType);
        response.put("debugOtp", otpCode); // REMOVE IN PRODUCTION

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        String mobile = request.get("mobile");
        String otp = request.get("otp");

        if (mobile == null || otp == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Mobile and OTP are required");
            return ResponseEntity.badRequest().body(error);
        }

        // Verify OTP
        if (!otpService.verifyOtp(mobile, otp)) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid or expired OTP");
            return ResponseEntity.badRequest().body(error);
        }

        // Find or create user
        User user = userRepository.findByMobile(mobile)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setMobile(mobile);
                    newUser.setPhone(mobile); // Set phone field same as mobile
                    newUser.setRole(User.UserRole.CUSTOMER);
                    newUser.setProfileCompleted(false);
                    return userRepository.save(newUser);
                });

        // Generate JWT token
        String token = jwtUtil.generateToken(mobile, user.getId(), user.getRole().name());

        // Build response
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("mobile", user.getMobile());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole());
        userData.put("profileCompleted", user.isProfileCompleted());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("user", userData);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null || !jwtUtil.validateToken(token)) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid token");
            return ResponseEntity.badRequest().body(error);
        }

        String mobile = jwtUtil.extractMobile(token);
        Long userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);

        String newToken = jwtUtil.generateToken(mobile, userId, role);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", newToken);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("mobile", user.getMobile());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole());
        userData.put("profileCompleted", user.isProfileCompleted());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("user", userData);

        return ResponseEntity.ok(response);
    }
}
