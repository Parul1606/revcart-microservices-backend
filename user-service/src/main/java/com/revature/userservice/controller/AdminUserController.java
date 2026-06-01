package com.revature.userservice.controller;

import com.revature.userservice.entity.User;
import com.revature.userservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        long totalUsers = userRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", totalUsers);
        stats.put("adminUsers", 0);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<User> blockUser(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        return userRepository.findById(id)
                .map(user -> {
                    // In a real implementation, add isBlocked field to User entity
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return userRepository.findById(id)
                .map(user -> {
                    // Update user fields
                    if (request.containsKey("name")) {
                        String[] parts = request.get("name").split(" ", 2);
                        user.setFirstName(parts[0]);
                        if (parts.length > 1) {
                            user.setLastName(parts[1]);
                        }
                    }
                    if (request.containsKey("email")) {
                        user.setEmail(request.get("email"));
                    }
                    if (request.containsKey("phone")) {
                        user.setPhone(request.get("phone"));
                    }
                    if (request.containsKey("role")) {
                        try {
                            user.setRole(User.UserRole.valueOf(request.get("role").toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            // Ignore invalid role
                        }
                    }
                    if (request.containsKey("status")) {
                        try {
                            user.setStatus(User.UserStatus.valueOf(request.get("status").toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            // Ignore invalid status
                        }
                    }

                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return userRepository.findById(id)
                .map(user -> {
                    if (request.containsKey("status")) {
                        try {
                            User.UserStatus newStatus = User.UserStatus.valueOf(request.get("status").toUpperCase());
                            user.setStatus(newStatus);
                            User updatedUser = userRepository.save(user);
                            return ResponseEntity.ok(updatedUser);
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.badRequest().body(user);
                        }
                    }
                    return ResponseEntity.badRequest().body(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Admin User Service");
        return ResponseEntity.ok(health);
    }
}
