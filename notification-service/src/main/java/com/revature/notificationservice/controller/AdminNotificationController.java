package com.revature.notificationservice.controller;

import com.revature.notificationservice.dto.SendNotificationRequest;
import com.revature.notificationservice.entity.Notification;
import com.revature.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notifications")
public class AdminNotificationController {

    private final NotificationService notificationService;

    public AdminNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, Object>> broadcastNotification(@RequestBody SendNotificationRequest request) {
        // In a real implementation, send to all users
        // For now, just return success
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Broadcast notification sent");
        response.put("title", request.getTitle());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNotificationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNotifications", "N/A");
        stats.put("unreadNotifications", "N/A");

        return ResponseEntity.ok(stats);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "50") int size) {
        List<Notification> notifications = notificationService.getAllNotifications(page, size);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(@PathVariable String type) {
        List<Notification> notifications = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody SendNotificationRequest request) {
        Notification notification = notificationService.createNotification(
                request.getUserId(),
                request.getTitle(),
                request.getMessage(),
                request.getType());
        return ResponseEntity.ok(notification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Notification deleted successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Admin Notification Service");
        return ResponseEntity.ok(health);
    }
}
