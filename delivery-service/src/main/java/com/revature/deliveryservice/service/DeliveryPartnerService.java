package com.revature.deliveryservice.service;

import com.revature.deliveryservice.entity.DeliveryPartner;
import com.revature.deliveryservice.repository.DeliveryPartnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DeliveryPartnerService {

    private final DeliveryPartnerRepository deliveryPartnerRepository;

    public DeliveryPartnerService(DeliveryPartnerRepository deliveryPartnerRepository) {
        this.deliveryPartnerRepository = deliveryPartnerRepository;
    }

    public List<DeliveryPartner> getAllDeliveryPartners() {
        return deliveryPartnerRepository.findAll();
    }

    public DeliveryPartner getDeliveryPartnerById(Long id) {
        return deliveryPartnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery partner not found"));
    }

    public DeliveryPartner createDeliveryPartner(DeliveryPartner deliveryPartner) {
        return deliveryPartnerRepository.save(deliveryPartner);
    }

    public DeliveryPartner updateDeliveryPartner(Long id, DeliveryPartner updatedPartner) {
        DeliveryPartner existing = getDeliveryPartnerById(id);

        // Update only the fields that exist
        if (updatedPartner.getEmail() != null) {
            existing.setEmail(updatedPartner.getEmail());
        }
        if (updatedPartner.getPhone() != null) {
            existing.setPhone(updatedPartner.getPhone());
        }
        if (updatedPartner.getVehicleType() != null) {
            existing.setVehicleType(updatedPartner.getVehicleType());
        }
        if (updatedPartner.getVehicleNumber() != null) {
            existing.setVehicleNumber(updatedPartner.getVehicleNumber());
        }
        if (updatedPartner.getStatus() != null) {
            existing.setStatus(updatedPartner.getStatus());
        }
        if (updatedPartner.getIsAvailable() != null) {
            existing.setIsAvailable(updatedPartner.getIsAvailable());
        }

        return deliveryPartnerRepository.save(existing);
    }

    public void deleteDeliveryPartner(Long id) {
        deliveryPartnerRepository.deleteById(id);
    }

    public List<DeliveryPartner> getAvailablePartners() {
        return deliveryPartnerRepository.findAll().stream()
                .filter(partner -> Boolean.TRUE.equals(partner.getIsAvailable()))
                .toList();
    }

    public DeliveryPartner updateAvailability(Long id, Boolean isAvailable) {
        DeliveryPartner partner = getDeliveryPartnerById(id);
        partner.setIsAvailable(isAvailable);
        return deliveryPartnerRepository.save(partner);
    }

    public DeliveryPartner registerDeliveryPartner(DeliveryPartner deliveryPartner) {
        deliveryPartner.setStatus("pending");
        deliveryPartner.setIsAvailable(false);
        deliveryPartner.setRating(5.0);
        deliveryPartner.setTotalDeliveries(0);
        return deliveryPartnerRepository.save(deliveryPartner);
    }
}
