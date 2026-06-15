package com.revature.paymentservice.controller;

import com.revature.paymentservice.dto.CreatePaymentRequest;
import com.revature.paymentservice.dto.PaymentResponse;
import com.revature.paymentservice.dto.VerifyPaymentRequest;
import com.revature.paymentservice.entity.Payment;
import com.revature.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<PaymentResponse> createPaymentOrder(
            @RequestBody CreatePaymentRequest request,
            @RequestParam Long orderId) {
        try {
            PaymentResponse response = paymentService.createPaymentOrder(request, orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            PaymentResponse errorResponse = new PaymentResponse();
            errorResponse.setStatus("FAILED");
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody VerifyPaymentRequest request) {
        try {
            PaymentResponse response = paymentService.verifyPayment(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("paymentId", response.getPaymentId());
            result.put("orderId", response.getOrderId());
            result.put("status", response.getStatus());
            result.put("message", response.getMessage());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable Long orderId) {
        try {
            Payment payment = paymentService.getPaymentByOrderId(orderId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getUserPayments(@PathVariable Long userId) {
        List<Payment> payments = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> processRefund(
            @RequestParam Long paymentId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String reason) {
        try {
            PaymentResponse response = paymentService.processRefund(paymentId, amount, reason);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("paymentId", response.getPaymentId());
            result.put("amount", response.getAmount());
            result.put("status", response.getStatus());
            result.put("message", response.getMessage());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        try {
            // TODO: Implement webhook signature verification
            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Payment Service");
        return ResponseEntity.ok(health);
    }
}
