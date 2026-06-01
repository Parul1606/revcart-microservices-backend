package com.revature.orderservice.dto;

public class AssignDeliveryRequest {
    private Long deliveryPartnerId;
    private String deliveryPartnerName;

    public AssignDeliveryRequest() {
    }

    public Long getDeliveryPartnerId() {
        return deliveryPartnerId;
    }

    public void setDeliveryPartnerId(Long deliveryPartnerId) {
        this.deliveryPartnerId = deliveryPartnerId;
    }

    public String getDeliveryPartnerName() {
        return deliveryPartnerName;
    }

    public void setDeliveryPartnerName(String deliveryPartnerName) {
        this.deliveryPartnerName = deliveryPartnerName;
    }
}
