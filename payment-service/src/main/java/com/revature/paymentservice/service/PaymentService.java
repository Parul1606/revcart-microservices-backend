package com.revature.paymentservice.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.revature.paymentservice.dto.CreatePaymentRequest;
import com.revature.paymentservice.dto.PaymentResponse;
import com.revature.paymentservice.dto.VerifyPaymentRequest;
import com.revature.paymentservice.entity.Payment;
import com.revature.paymentservice.repository.PaymentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse createPaymentOrder(CreatePaymentRequest request, Long orderId) {
        try {
            // Create actual Razorpay order
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount().multiply(new BigDecimal("100")).intValue()); // Convert to
                                                                                                        // paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_" + orderId);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            // Save payment record
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setUserId(request.getUserId());
            payment.setAmount(request.getAmount());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setPaymentGateway("RAZORPAY");
            payment.setCurrency("INR");
            payment.setStatus("PENDING");
            payment.setRazorpayOrderId(razorpayOrder.get("id"));

            Payment savedPayment = paymentRepository.save(payment);

            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(savedPayment.getId());
            response.setOrderId(savedPayment.getOrderId());
            response.setRazorpayOrderId(savedPayment.getRazorpayOrderId());
            response.setAmount(savedPayment.getAmount());
            response.setStatus(savedPayment.getStatus());
            response.setMessage("Payment order created successfully");
            response.setKey(razorpayKeyId); // Frontend needs this
            response.setCurrency("INR");

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }

    public PaymentResponse verifyPayment(VerifyPaymentRequest request) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // In production, verify Razorpay signature here
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus("SUCCESS");
        payment.setCompletedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(savedPayment.getId());
        response.setOrderId(savedPayment.getOrderId());
        response.setRazorpayOrderId(savedPayment.getRazorpayOrderId());
        response.setAmount(savedPayment.getAmount());
        response.setStatus(savedPayment.getStatus());
        response.setMessage("Payment verified successfully");

        return response;
    }

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order"));
    }

    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public PaymentResponse processRefund(Long paymentId, BigDecimal amount, String reason) {
        Payment payment = getPaymentById(paymentId);

        if (!"SUCCESS".equals(payment.getStatus())) {
            throw new RuntimeException("Cannot refund non-successful payment");
        }

        // In production, process actual Razorpay refund here
        payment.setRefundId("rfnd_" + System.currentTimeMillis());
        payment.setRefundAmount(amount);
        payment.setStatus("REFUNDED");

        Payment savedPayment = paymentRepository.save(payment);

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(savedPayment.getId());
        response.setOrderId(savedPayment.getOrderId());
        response.setAmount(amount);
        response.setStatus("REFUNDED");
        response.setMessage("Refund processed successfully");

        return response;
    }
}
