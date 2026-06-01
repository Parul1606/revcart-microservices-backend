package com.revature.orderservice.dto;

import com.revature.orderservice.entity.Order.OrderStatus;

public class UpdateOrderStatusRequest {
    private OrderStatus status;
    private String note;

    public UpdateOrderStatusRequest() {
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
