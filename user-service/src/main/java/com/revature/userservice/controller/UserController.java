package com.revature.userservice.controller;

import com.revature.common.util.JwtUtil;
import com.revature.userservice.entity.Address;
import com.revature.userservice.entity.User;
import com.revature.userservice.repository.AddressRepository;
import com.revature.userservice.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepository, AddressRepository addressRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/profile/basic")
    public ResponseEntity<Map<String, Object>> updateBasicInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {

        String token = authHeader.substring(7);
        String mobile = jwtUtil.extractMobile(token);

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String firstName = request.get("firstName");
        String lastName = request.get("lastName");

        if (firstName == null || firstName.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "First name is required");
            return ResponseEntity.badRequest().body(error);
        }

        user.setFirstName(firstName);
        user.setLastName(lastName != null ? lastName : "");
        user.setProfileCompleted(true);

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile updated successfully");
        response.put("user", Map.of(
                "id", user.getId(),
                "mobile", user.getMobile(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "profileCompleted", user.isProfileCompleted()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile/location")
    public ResponseEntity<Map<String, Object>> updateLocation(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {

        String token = authHeader.substring(7);
        String mobile = jwtUtil.extractMobile(token);

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            if (request.get("latitude") != null) {
                user.setLatitude(Double.parseDouble(request.get("latitude")));
            }
            if (request.get("longitude") != null) {
                user.setLongitude(Double.parseDouble(request.get("longitude")));
            }
            if (request.get("address") != null) {
                user.setAddress(request.get("address"));
            }

            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Location updated successfully");

            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid latitude/longitude format");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/profile/additional")
    public ResponseEntity<Map<String, Object>> updateAdditionalInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {

        String token = authHeader.substring(7);
        String mobile = jwtUtil.extractMobile(token);

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.get("email") != null) {
            user.setEmail(request.get("email"));
        }

        if (request.get("dateOfBirth") != null) {
            try {
                LocalDate dob = LocalDate.parse(request.get("dateOfBirth"),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                user.setDateOfBirth(dob);
            } catch (Exception e) {
                // Ignore if date format is invalid
            }
        }

        if (request.get("gender") != null) {
            try {
                user.setGender(User.Gender.valueOf(request.get("gender")));
            } catch (Exception e) {
                // Ignore if gender value is invalid
            }
        }

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Additional info updated successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/onboarding-status")
    public ResponseEntity<Map<String, Object>> getOnboardingStatus(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String mobile = jwtUtil.extractMobile(token);

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("profileCompleted", user.isProfileCompleted());
        response.put("hasBasicInfo", user.getFirstName() != null && !user.getFirstName().isEmpty());
        response.put("hasLocation", user.getLatitude() != null && user.getLongitude() != null);
        response.put("hasAdditionalInfo",
                user.getEmail() != null || user.getDateOfBirth() != null || user.getGender() != null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{phone}")
    public ResponseEntity<Map<String, Object>> getProfileByPhone(@PathVariable String phone) {
        User user = userRepository.findByMobile(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> userData = buildUserResponse(user);
        return ResponseEntity.ok(userData);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String mobile = jwtUtil.extractMobile(token);

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> userData = buildUserResponse(user);
        return ResponseEntity.ok(userData);
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {

        String token = authHeader.substring(7);
        String mobile = jwtUtil.extractMobile(token);

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update all provided fields
        if (request.get("firstName") != null) {
            user.setFirstName(request.get("firstName"));
        }
        if (request.get("lastName") != null) {
            user.setLastName(request.get("lastName"));
        }
        if (request.get("email") != null) {
            user.setEmail(request.get("email"));
        }
        if (request.get("address") != null) {
            user.setAddress(request.get("address"));
        }
        if (request.get("avatarUrl") != null) {
            user.setAvatarUrl(request.get("avatarUrl"));
        }

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile updated successfully");
        response.put("user", buildUserResponse(user));

        return ResponseEntity.ok(response);
    }

    // ==================== Address Endpoints ====================

    @GetMapping("/addresses")
    public ResponseEntity<List<Address>> getAddresses(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String mobile = jwtUtil.extractMobile(token);

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Address> addresses = addressRepository.findByUserId(user.getId());
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/addresses")
    public ResponseEntity<Map<String, Object>> addAddress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Address addressRequest) {

        String token = authHeader.substring(7);
        String mobile = jwtUtil.extractMobile(token);

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = new Address();
        address.setUser(user);
        address.setLabel(addressRequest.getLabel() != null ? addressRequest.getLabel() : "Home");
        address.setAddressLine1(addressRequest.getAddressLine1());
        address.setAddressLine2(addressRequest.getAddressLine2());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setPincode(addressRequest.getPincode());
        address.setLandmark(addressRequest.getLandmark());
        address.setType(addressRequest.getType() != null ? addressRequest.getType() : Address.AddressType.HOME);
        address.setIsDefault(addressRequest.getIsDefault() != null ? addressRequest.getIsDefault() : false);

        // If this is set as default, unset other defaults
        if (address.getIsDefault()) {
            List<Address> existingAddresses = addressRepository.findByUserId(user.getId());
            existingAddresses.forEach(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
        }

        Address savedAddress = addressRepository.save(address);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Address added successfully");
        response.put("address", savedAddress);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody Address addressRequest) {

        String token = authHeader.substring(7);
        String mobile = jwtUtil.extractMobile(token);

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Verify address belongs to user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (addressRequest.getLabel() != null)
            address.setLabel(addressRequest.getLabel());
        if (addressRequest.getAddressLine1() != null)
            address.setAddressLine1(addressRequest.getAddressLine1());
        if (addressRequest.getAddressLine2() != null)
            address.setAddressLine2(addressRequest.getAddressLine2());
        if (addressRequest.getCity() != null)
            address.setCity(addressRequest.getCity());
        if (addressRequest.getState() != null)
            address.setState(addressRequest.getState());
        if (addressRequest.getPincode() != null)
            address.setPincode(addressRequest.getPincode());
        if (addressRequest.getLandmark() != null)
            address.setLandmark(addressRequest.getLandmark());
        if (addressRequest.getType() != null)
            address.setType(addressRequest.getType());
        if (addressRequest.getIsDefault() != null)
            address.setIsDefault(addressRequest.getIsDefault());

        // If this is set as default, unset other defaults
        if (address.getIsDefault()) {
            List<Address> existingAddresses = addressRepository.findByUserId(user.getId());
            existingAddresses.forEach(addr -> {
                if (!addr.getId().equals(id)) {
                    addr.setIsDefault(false);
                    addressRepository.save(addr);
                }
            });
        }

        Address savedAddress = addressRepository.save(address);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Address updated successfully");
        response.put("address", savedAddress);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Map<String, Object>> deleteAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String token = authHeader.substring(7);
        String mobile = jwtUtil.extractMobile(token);

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Verify address belongs to user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        addressRepository.delete(address);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Address deleted successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/addresses/{id}/default")
    public ResponseEntity<Map<String, Object>> setDefaultAddress(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String token = authHeader.substring(7);
        String mobile = jwtUtil.extractMobile(token);

        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Verify address belongs to user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        // Unset all other defaults
        List<Address> existingAddresses = addressRepository.findByUserId(user.getId());
        existingAddresses.forEach(addr -> {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        });

        // Set this as default
        address.setIsDefault(true);
        addressRepository.save(address);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Default address updated successfully");

        return ResponseEntity.ok(response);
    }

    // Helper method to build user response
    private Map<String, Object> buildUserResponse(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("mobile", user.getMobile());
        userData.put("phone", user.getPhone());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("email", user.getEmail());
        userData.put("avatarUrl", user.getAvatarUrl());
        userData.put("dateOfBirth", user.getDateOfBirth());
        userData.put("gender", user.getGender());
        userData.put("latitude", user.getLatitude());
        userData.put("longitude", user.getLongitude());
        userData.put("address", user.getAddress());
        userData.put("role", user.getRole());
        userData.put("profileCompleted", user.isProfileCompleted());
        userData.put("mobileVerified", user.isMobileVerified());
        return userData;
    }
}
