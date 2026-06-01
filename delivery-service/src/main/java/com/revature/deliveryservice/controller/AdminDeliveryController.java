package com.revature.deliveryservice.controller;

import com.revature.deliveryservice.entity.DeliveryPartner;
import com.revature.deliveryservice.service.DeliveryPartnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/delivery-partners")
public class AdminDeliveryController {

    private final DeliveryPartnerService deliveryPartnerService;

    public AdminDeliveryController(DeliveryPartnerService deliveryPartnerService) {
        this.deliveryPartnerService = deliveryPartnerService;
    }

    @GetMapping
    public ResponseEntity<List<DeliveryPartner>> getAllPartners() {
        List<DeliveryPartner> partners = deliveryPartnerService.getAllDeliveryPartners();
        return ResponseEntity.ok(partners);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<DeliveryPartner> approvePartner(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            DeliveryPartner partner = deliveryPartnerService.getDeliveryPartnerById(id);
            String status = request.get("status"); // "approved" or "rejected"
            partner.setStatus(status != null ? status : "approved");
            DeliveryPartner updated = deliveryPartnerService.updateDeliveryPartner(id, partner);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPartnerStats() {
        List<DeliveryPartner> allPartners = deliveryPartnerService.getAllDeliveryPartners();
        List<DeliveryPartner> available = deliveryPartnerService.getAvailablePartners();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPartners", allPartners.size());
        stats.put("availablePartners", available.size());
        stats.put("activePartners", available.size());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/partners/{id}/stats")
    public ResponseEntity<Map<String, Object>> getPartnerDetailedStats(@PathVariable Long id) {
        try {
            DeliveryPartner partner = deliveryPartnerService.getDeliveryPartnerById(id);

            Map<String, Object> stats = new HashMap<>();
            stats.put("partnerId", partner.getId());
            stats.put("name", partner.getFirstName() + " " + partner.getLastName());
            stats.put("totalDeliveries", partner.getTotalDeliveries());
            stats.put("rating", partner.getRating());
            stats.put("status", partner.getStatus());
            stats.put("isAvailable", partner.getIsAvailable());

            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/partners/{id}/orders")
    public ResponseEntity<Map<String, Object>> getPartnerOrders(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("partnerId", id);
        response.put("orders", List.of());
        response.put("message", "Partner order history available");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getDeliveryStatistics() {
        List<DeliveryPartner> allPartners = deliveryPartnerService.getAllDeliveryPartners();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalPartners", allPartners.size());
        statistics.put("activePartners", allPartners.stream().filter(p -> "approved".equals(p.getStatus())).count());
        statistics.put("pendingApproval", allPartners.stream().filter(p -> "pending".equals(p.getStatus())).count());
        statistics.put("totalDeliveries",
                allPartners.stream().mapToInt(p -> p.getTotalDeliveries() != null ? p.getTotalDeliveries() : 0).sum());

        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<Map<String, Object>> getUnassignedOrders() {
        Map<String, Object> response = new HashMap<>();
        response.put("unassignedOrders", List.of());
        response.put("count", 0);
        response.put("message", "No unassigned orders");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/partners")
    public ResponseEntity<List<DeliveryPartner>> getAllPartnersDetailed() {
        List<DeliveryPartner> partners = deliveryPartnerService.getAllDeliveryPartners();
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Admin Delivery Service");
        return ResponseEntity.ok(health);
    }
}
