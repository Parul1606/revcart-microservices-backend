package com.revature.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AddressType type = AddressType.HOME;

    @Column(nullable = false, length = 50)
    private String label;

    @Column(nullable = false, length = 255)
    private String addressLine1;

    @Column(length = 255)
    private String addressLine2;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 10)
    private String pincode;

    @Column(length = 255)
    private String landmark;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private Boolean isDefault = false;

    public Address() {
    }

    public Address(Long id, User user, AddressType type, String label, String addressLine1, String addressLine2,
            String city, String state, String pincode, String landmark, Double latitude, Double longitude,
            Boolean isDefault) {
        this.id = id;
        this.user = user;
        this.type = type;
        this.label = label;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.landmark = landmark;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDefault = isDefault;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    // Builder
    public static AddressBuilder builder() {
        return new AddressBuilder();
    }

    public static class AddressBuilder {
        private Long id;
        private User user;
        private AddressType type = AddressType.HOME;
        private String label;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String pincode;
        private String landmark;
        private Double latitude;
        private Double longitude;
        private Boolean isDefault = false;

        AddressBuilder() {
        }

        public AddressBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AddressBuilder user(User user) {
            this.user = user;
            return this;
        }

        public AddressBuilder type(AddressType type) {
            this.type = type;
            return this;
        }

        public AddressBuilder label(String label) {
            this.label = label;
            return this;
        }

        public AddressBuilder addressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
            return this;
        }

        public AddressBuilder addressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
            return this;
        }

        public AddressBuilder city(String city) {
            this.city = city;
            return this;
        }

        public AddressBuilder state(String state) {
            this.state = state;
            return this;
        }

        public AddressBuilder pincode(String pincode) {
            this.pincode = pincode;
            return this;
        }

        public AddressBuilder landmark(String landmark) {
            this.landmark = landmark;
            return this;
        }

        public AddressBuilder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public AddressBuilder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public AddressBuilder isDefault(Boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public Address build() {
            return new Address(id, user, type, label, addressLine1, addressLine2, city, state, pincode, landmark,
                    latitude, longitude, isDefault);
        }
    }

    public enum AddressType {
        HOME, WORK, OTHER
    }
}
