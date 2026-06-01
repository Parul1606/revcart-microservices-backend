package com.revature.orderservice.controller;

import com.revature.orderservice.dto.AssignDeliveryRequest;
import com.revature.orderservice.dto.OrderResponse;
import com.revature.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery/orders")
public class DeliveryOrderController {

    private final OrderService orderService;

    public DeliveryOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<OrderResponse>> getAvailableOrders(
            @RequestHeader(value = "X-Delivery-Partner-Id", required = false, defaultValue = "1") Long partnerId) {
        try {
            // Get orders that are confirmed but not yet assigned to any delivery partner
            List<OrderResponse> orders = orderService.getAvailableOrdersForDelivery();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch available orders: " + e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<OrderResponse> getActiveOrder(
            @RequestHeader(value = "X-Delivery-Partner-Id", required = false, defaultValue = "1") Long partnerId) {
        try {
            List<OrderResponse> activeOrders = orderService.getActiveOrderForDeliveryPartner(partnerId);
            if (activeOrders.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(activeOrders.get(0));
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch active order: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponse>> getDeliveryHistory(
            @RequestHeader(value = "X-Delivery-Partner-Id", required = false, defaultValue = "1") Long partnerId) {
        try {
            List<OrderResponse> history = orderService.getDeliveryHistory(partnerId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch delivery history: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDeliveryStats(
            @RequestHeader(value = "X-Delivery-Partner-Id", required = false, defaultValue = "1") Long partnerId) {
        try {
            List<OrderResponse> history = orderService.getDeliveryHistory(partnerId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalDeliveries", history.size());
            stats.put("todayDeliveries", 0); // Simplified - would need date filtering
            stats.put("earnings", 0); // Simplified - would need calculation

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch delivery stats: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<OrderResponse> acceptOrder(
            @PathVariable Long id,
            @RequestHeader(value = "X-Delivery-Partner-Id", required = false, defaultValue = "1") Long partnerId) {
        try {
            // Simplified - in full implementation, would check if partner is available
            AssignDeliveryRequest request = new AssignDeliveryRequest();
            request.setDeliveryPartnerId(partnerId);
            request.setDeliveryPartnerName("Delivery Partner " + partnerId);

            OrderResponse order = orderService.assignDeliveryPartner(id, request);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to accept order: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "X-Delivery-Partner-Id", required = false, defaultValue = "1") Long partnerId) {
        try {
            // Simplified - in full implementation, would mark order as rejected by this
            // partner
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order rejected");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reject order: " + e.getMessage());
        }
    }
}
