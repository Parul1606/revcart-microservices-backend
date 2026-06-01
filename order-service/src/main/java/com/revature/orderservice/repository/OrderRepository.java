package com.revature.orderservice.repository;

import com.revature.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);

    List<Order> findByUserId(Long userId);

    List<Order> findByDeliveryPartnerId(Long deliveryPartnerId);
}
