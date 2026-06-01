package com.revature.productservice.dto;

public class UpdateStockRequest {
    private Integer quantity;
    private String operation; // ADD, SET, SUBTRACT

    public UpdateStockRequest() {
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
