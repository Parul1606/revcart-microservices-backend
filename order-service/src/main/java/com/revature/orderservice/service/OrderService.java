package com.revature.orderservice.service;

import com.revature.orderservice.dto.*;
import com.revature.orderservice.entity.Order;
import com.revature.orderservice.entity.OrderItem;
import com.revature.orderservice.entity.Order.OrderStatus;
import com.revature.orderservice.entity.Order.PaymentStatus;
import com.revature.orderservice.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        String orderId = "ORD" + System.currentTimeMillis();

        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(request.getUserId());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setSubtotal(request.getSubtotal());
        order.setDiscount(request.getDiscount());
        order.setDeliveryFee(request.getDeliveryFee());
        order.setTax(request.getTax());
        order.setTotal(request.getTotal());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setDeliveryInstructions(request.getDeliveryInstructions());
        order.setNotes(request.getNotes());
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(2));
        order.setOtp(String.valueOf(1000 + new Random().nextInt(9000)));

        for (OrderItemDTO itemDTO : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemDTO.getProductId());
            orderItem.setProductName(itemDTO.getProductName());
            orderItem.setProductImage(itemDTO.getProductImage());
            orderItem.setPrice(itemDTO.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setTotalPrice(itemDTO.getTotalPrice());
            orderItem.setUnit(itemDTO.getUnit());
            orderItem.setCategory(itemDTO.getCategory());
            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        return mapToOrderResponse(order);
    }

    public OrderResponse getOrderByOrderId(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with order ID: " + orderId));
        return mapToOrderResponse(order);
    }

    public OrderResponse cancelOrder(Long id, CancelOrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel this order");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(request.getReason());

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    public OrderTrackingResponse trackOrder(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderTrackingResponse response = new OrderTrackingResponse();
        response.setOrderId(order.getOrderId());
        response.setCurrentStatus(order.getOrderStatus());
        response.setOrderDate(order.getCreatedAt());
        response.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        response.setActualDeliveryTime(order.getActualDeliveryTime());
        response.setDeliveryPartnerName(order.getDeliveryPartnerName());
        return response;
    }

    public OrderResponse updateOrderStatus(Long id, OrderStatus status, String note) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(status);
        if (status == OrderStatus.DELIVERED) {
            order.setActualDeliveryTime(LocalDateTime.now());
        }

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    public Page<OrderResponse> getAllOrdersForAdmin(Pageable pageable, String search,
            String status, String paymentStatus, String paymentMethod, String startDate, String endDate) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(this::mapToOrderResponse);
    }

    public OrderAnalyticsDTO getOrderAnalytics() {
        List<Order> allOrders = orderRepository.findAll();
        OrderAnalyticsDTO analytics = new OrderAnalyticsDTO();

        analytics.setTotalOrders((long) allOrders.size());
        analytics.setPendingOrders(allOrders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.PENDING).count());
        analytics.setConfirmedOrders(allOrders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.CONFIRMED).count());
        analytics.setDeliveredOrders(allOrders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED).count());
        analytics.setCancelledOrders(allOrders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.CANCELLED).count());

        BigDecimal totalRevenue = allOrders.stream()
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        analytics.setTotalRevenue(totalRevenue);

        BigDecimal avgOrderValue = allOrders.isEmpty() ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(allOrders.size()), 2, java.math.RoundingMode.HALF_UP);
        analytics.setAverageOrderValue(avgOrderValue);

        return analytics;
    }

    public OrderResponse updateOrderStatusByAdmin(Long id, UpdateOrderStatusRequest request) {
        return updateOrderStatus(id, request.getStatus(), request.getNote());
    }

    public OrderResponse assignDeliveryPartner(Long id, AssignDeliveryRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setDeliveryPartnerId(request.getDeliveryPartnerId());
        order.setDeliveryPartnerName(request.getDeliveryPartnerName());
        order.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getAvailableOrdersForDelivery() {
        return orderRepository.findAll().stream()
                .filter(o -> o.getOrderStatus() == Order.OrderStatus.CONFIRMED && o.getDeliveryPartnerId() == null)
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getActiveOrderForDeliveryPartner(Long partnerId) {
        return orderRepository.findByDeliveryPartnerId(partnerId).stream()
                .filter(o -> o.getOrderStatus() == Order.OrderStatus.OUT_FOR_DELIVERY)
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getDeliveryHistory(Long partnerId) {
        return orderRepository.findByDeliveryPartnerId(partnerId).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    OrderItemDTO dto = new OrderItemDTO();
                    dto.setId(item.getId());
                    dto.setProductId(item.getProductId());
                    dto.setProductName(item.getProductName());
                    dto.setProductImage(item.getProductImage());
                    dto.setPrice(item.getPrice());
                    dto.setQuantity(item.getQuantity());
                    dto.setTotalPrice(item.getTotalPrice());
                    dto.setUnit(item.getUnit());
                    dto.setCategory(item.getCategory());
                    return dto;
                })
                .collect(Collectors.toList());

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUserId());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerPhone(order.getCustomerPhone());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setItems(itemDTOs);
        response.setSubtotal(order.getSubtotal());
        response.setDiscount(order.getDiscount());
        response.setDeliveryFee(order.getDeliveryFee());
        response.setTax(order.getTax());
        response.setTotal(order.getTotal());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setOrderStatus(order.getOrderStatus());
        response.setCancellationReason(order.getCancellationReason());
        response.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        response.setActualDeliveryTime(order.getActualDeliveryTime());
        response.setDeliveryPartnerId(order.getDeliveryPartnerId());
        response.setDeliveryPartnerName(order.getDeliveryPartnerName());
        response.setOtp(order.getOtp());
        response.setDeliveryInstructions(order.getDeliveryInstructions());
        response.setNotes(order.getNotes());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
}
