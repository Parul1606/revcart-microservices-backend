package com.revature.deliveryservice.controller;

import com.revature.deliveryservice.entity.DeliveryPartner;
import com.revature.deliveryservice.service.DeliveryPartnerService;
import com.revature.deliveryservice.service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;
    private final S3Service s3Service;

    public DeliveryPartnerController(DeliveryPartnerService deliveryPartnerService, S3Service s3Service) {
        this.deliveryPartnerService = deliveryPartnerService;
        this.s3Service = s3Service;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerDeliveryPartner(@RequestBody DeliveryPartner deliveryPartner) {
        try {
            DeliveryPartner registered = deliveryPartnerService.registerDeliveryPartner(deliveryPartner);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Delivery partner registered successfully. Pending admin approval.");
            response.put("partner", registered);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Register delivery partner with file uploads (matches frontend FormData)
     * This endpoint accepts multipart/form-data with actual file uploads
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> registerWithFiles(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String dateOfBirth,
            @RequestParam String gender,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String addressLine1,
            @RequestParam(required = false) String addressLine2,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String pincode,
            @RequestParam String aadharNumber,
            @RequestParam String drivingLicenseNumber,
            @RequestParam String drivingLicenseExpiry,
            @RequestParam String vehicleType,
            @RequestParam String vehicleNumber,
            @RequestParam String vehicleMake,
            @RequestParam String vehicleModel,
            @RequestParam String accountHolderName,
            @RequestParam String accountNumber,
            @RequestParam String ifscCode,
            @RequestParam String bankName,
            @RequestParam(required = false) MultipartFile aadharFrontImage,
            @RequestParam(required = false) MultipartFile aadharBackImage,
            @RequestParam(required = false) MultipartFile drivingLicenseImage,
            @RequestParam(required = false) MultipartFile vehicleRCImage,
            @RequestParam(required = false) MultipartFile profilePhoto) {
        try {
            // Upload files to S3 and get URLs
            String aadharFrontUrl = null;
            String aadharBackUrl = null;
            String drivingLicenseUrl = null;
            String vehicleRCUrl = null;
            String profilePhotoUrl = null;

            if (aadharFrontImage != null && !aadharFrontImage.isEmpty()) {
                s3Service.validateFile(aadharFrontImage, 5); // 5MB max
                aadharFrontUrl = s3Service.uploadFile(aadharFrontImage, "delivery/aadhar");
            }

            if (aadharBackImage != null && !aadharBackImage.isEmpty()) {
                s3Service.validateFile(aadharBackImage, 5);
                aadharBackUrl = s3Service.uploadFile(aadharBackImage, "delivery/aadhar");
            }

            if (drivingLicenseImage != null && !drivingLicenseImage.isEmpty()) {
                s3Service.validateFile(drivingLicenseImage, 5);
                drivingLicenseUrl = s3Service.uploadFile(drivingLicenseImage, "delivery/licenses");
            }

            if (vehicleRCImage != null && !vehicleRCImage.isEmpty()) {
                s3Service.validateFile(vehicleRCImage, 5);
                vehicleRCUrl = s3Service.uploadFile(vehicleRCImage, "delivery/vehicles");
            }

            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                s3Service.validateFile(profilePhoto, 5);
                profilePhotoUrl = s3Service.uploadFile(profilePhoto, "delivery/profiles");
            }

            // Create DeliveryPartner entity
            DeliveryPartner deliveryPartner = new DeliveryPartner();
            deliveryPartner.setFirstName(firstName);
            deliveryPartner.setLastName(lastName);
            deliveryPartner.setDateOfBirth(LocalDate.parse(dateOfBirth));
            deliveryPartner.setGender(gender);
            deliveryPartner.setEmail(email);
            deliveryPartner.setPhone(phone);
            deliveryPartner.setAddressLine1(addressLine1);
            deliveryPartner.setAddressLine2(addressLine2);
            deliveryPartner.setCity(city);
            deliveryPartner.setState(state);
            deliveryPartner.setPincode(pincode);
            deliveryPartner.setAadharNumber(aadharNumber);
            deliveryPartner.setAadharFrontUrl(aadharFrontUrl != null ? aadharFrontUrl : "");
            deliveryPartner.setAadharBackUrl(aadharBackUrl != null ? aadharBackUrl : "");
            deliveryPartner.setDrivingLicenseNumber(drivingLicenseNumber);
            deliveryPartner.setDrivingLicenseExpiry(LocalDate.parse(drivingLicenseExpiry));
            deliveryPartner.setDrivingLicenseUrl(drivingLicenseUrl != null ? drivingLicenseUrl : "");
            deliveryPartner.setVehicleType(vehicleType);
            deliveryPartner.setVehicleNumber(vehicleNumber);
            deliveryPartner.setVehicleMake(vehicleMake);
            deliveryPartner.setVehicleModel(vehicleModel);
            deliveryPartner.setVehicleRegistrationUrl(vehicleRCUrl != null ? vehicleRCUrl : "");
            deliveryPartner.setAccountHolderName(accountHolderName);
            deliveryPartner.setAccountNumber(accountNumber);
            deliveryPartner.setIfscCode(ifscCode);
            deliveryPartner.setBankName(bankName);
            deliveryPartner.setProfilePhotoUrl(profilePhotoUrl);

            // Register the delivery partner
            DeliveryPartner registered = deliveryPartnerService.registerDeliveryPartner(deliveryPartner);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Delivery partner registered successfully. Pending admin approval.");
            response.put("id", registered.getId());
            response.put("partner", registered);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Validation error: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/partners")
    public ResponseEntity<List<DeliveryPartner>> getAllDeliveryPartners() {
        List<DeliveryPartner> partners = deliveryPartnerService.getAllDeliveryPartners();
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/partners/stats")
    public ResponseEntity<Map<String, Object>> getPartnerStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("todayDeliveries", 8);
        stats.put("todayEarnings", 560);
        stats.put("weeklyDeliveries", 45);
        stats.put("weeklyEarnings", 4200);
        stats.put("averageRating", 4.8);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/partners/{id}")
    public ResponseEntity<DeliveryPartner> getDeliveryPartnerById(@PathVariable Long id) {
        try {
            DeliveryPartner partner = deliveryPartnerService.getDeliveryPartnerById(id);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/partners")
    public ResponseEntity<DeliveryPartner> createDeliveryPartner(@RequestBody DeliveryPartner deliveryPartner) {
        DeliveryPartner created = deliveryPartnerService.createDeliveryPartner(deliveryPartner);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/partners/{id}")
    public ResponseEntity<DeliveryPartner> updateDeliveryPartner(
            @PathVariable Long id,
            @RequestBody DeliveryPartner deliveryPartner) {
        try {
            DeliveryPartner updated = deliveryPartnerService.updateDeliveryPartner(id, deliveryPartner);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/partners/{id}")
    public ResponseEntity<Void> deleteDeliveryPartner(@PathVariable Long id) {
        deliveryPartnerService.deleteDeliveryPartner(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/partners/available")
    public ResponseEntity<List<DeliveryPartner>> getAvailablePartners() {
        List<DeliveryPartner> partners = deliveryPartnerService.getAvailablePartners();
        return ResponseEntity.ok(partners);
    }

    @PatchMapping("/partners/{id}/availability")
    public ResponseEntity<DeliveryPartner> updateAvailability(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean isAvailable = request.get("isAvailable");
            DeliveryPartner updated = deliveryPartnerService.updateAvailability(id, isAvailable);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/partners/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Delivery Service");
        return ResponseEntity.ok(health);
    }
}
