package com.revature.deliveryservice.repository;

import com.revature.deliveryservice.entity.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    Optional<DeliveryPartner> findByPhone(String phone);

    Optional<DeliveryPartner> findByEmail(String email);
}
