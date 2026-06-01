package com.revature.orderservice.controller;

import com.revature.orderservice.dto.*;
import com.revature.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        try {
            Sort sort = sortOrder.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);

            Page<OrderResponse> orders = orderService.getAllOrdersForAdmin(
                    pageable, search, status, paymentStatus, paymentMethod, startDate, endDate);

            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch orders: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Long id) {
        try {
            OrderResponse order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch order details: " + e.getMessage());
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<OrderAnalyticsDTO> getOrderAnalytics() {
        try {
            OrderAnalyticsDTO analytics = orderService.getOrderAnalytics();
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch order analytics: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {

        try {
            OrderResponse order = orderService.updateOrderStatusByAdmin(id, request);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update order status: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/assign-delivery")
    public ResponseEntity<OrderResponse> assignDeliveryPartner(
            @PathVariable Long id,
            @RequestBody AssignDeliveryRequest request) {

        try {
            OrderResponse order = orderService.assignDeliveryPartner(id, request);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign delivery partner: " + e.getMessage());
        }
    }
}
