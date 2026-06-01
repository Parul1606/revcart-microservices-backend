package com.revature.deliveryservice.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_partners")
public class DeliveryPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String addressLine1;

    private String addressLine2;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String pincode;

    @Column(nullable = false)
    private String aadharNumber;

    @Column(nullable = false)
    private String aadharFrontUrl;

    @Column(nullable = false)
    private String aadharBackUrl;

    @Column(nullable = false)
    private String drivingLicenseNumber;

    @Column(nullable = false)
    private String drivingLicenseUrl;

    @Column(nullable = false)
    private LocalDate drivingLicenseExpiry;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false)
    private String vehicleNumber;

    @Column(nullable = false)
    private String vehicleRegistrationUrl;

    @Column(nullable = false)
    private String vehicleMake;

    @Column(nullable = false)
    private String vehicleModel;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String ifscCode;

    @Column(nullable = false)
    private String bankName;

    private String profilePhotoUrl;

    @Column(nullable = false)
    private String status = "pending";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime approvedAt;

    @Column(length = 500)
    private String rejectedReason;

    @Column(nullable = false)
    private Double rating = 0.0;

    @Column(nullable = false)
    private Integer totalDeliveries = 0;

    @Column(nullable = false)
    private Boolean isAvailable = false;

    private Double currentLatitude;

    private Double currentLongitude;

    private Long activeOrderId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public DeliveryPartner() {
    }

    public DeliveryPartner(Long id, String firstName, String lastName, LocalDate dateOfBirth, String gender,
            String email, String phone, String addressLine1, String addressLine2, String city, String state,
            String pincode, String aadharNumber, String aadharFrontUrl, String aadharBackUrl,
            String drivingLicenseNumber, String drivingLicenseUrl, LocalDate drivingLicenseExpiry, String vehicleType,
            String vehicleNumber, String vehicleRegistrationUrl, String vehicleMake, String vehicleModel,
            String accountHolderName, String accountNumber, String ifscCode, String bankName, String profilePhotoUrl,
            String status, LocalDateTime createdAt, LocalDateTime approvedAt, String rejectedReason, Double rating,
            Integer totalDeliveries, Boolean isAvailable, Double currentLatitude, Double currentLongitude,
            Long activeOrderId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.aadharNumber = aadharNumber;
        this.aadharFrontUrl = aadharFrontUrl;
        this.aadharBackUrl = aadharBackUrl;
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.drivingLicenseUrl = drivingLicenseUrl;
        this.drivingLicenseExpiry = drivingLicenseExpiry;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.vehicleRegistrationUrl = vehicleRegistrationUrl;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.accountHolderName = accountHolderName;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.bankName = bankName;
        this.profilePhotoUrl = profilePhotoUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
        this.rejectedReason = rejectedReason;
        this.rating = rating;
        this.totalDeliveries = totalDeliveries;
        this.isAvailable = isAvailable;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.activeOrderId = activeOrderId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getAadharFrontUrl() {
        return aadharFrontUrl;
    }

    public void setAadharFrontUrl(String aadharFrontUrl) {
        this.aadharFrontUrl = aadharFrontUrl;
    }

    public String getAadharBackUrl() {
        return aadharBackUrl;
    }

    public void setAadharBackUrl(String aadharBackUrl) {
        this.aadharBackUrl = aadharBackUrl;
    }

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public String getDrivingLicenseUrl() {
        return drivingLicenseUrl;
    }

    public void setDrivingLicenseUrl(String drivingLicenseUrl) {
        this.drivingLicenseUrl = drivingLicenseUrl;
    }

    public LocalDate getDrivingLicenseExpiry() {
        return drivingLicenseExpiry;
    }

    public void setDrivingLicenseExpiry(LocalDate drivingLicenseExpiry) {
        this.drivingLicenseExpiry = drivingLicenseExpiry;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleRegistrationUrl() {
        return vehicleRegistrationUrl;
    }

    public void setVehicleRegistrationUrl(String vehicleRegistrationUrl) {
        this.vehicleRegistrationUrl = vehicleRegistrationUrl;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getTotalDeliveries() {
        return totalDeliveries;
    }

    public void setTotalDeliveries(Integer totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public Long getActiveOrderId() {
        return activeOrderId;
    }

    public void setActiveOrderId(Long activeOrderId) {
        this.activeOrderId = activeOrderId;
    }
}
