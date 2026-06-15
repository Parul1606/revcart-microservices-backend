package com.revature.cartservice.dto;

public class CartItemRequest {
    private Long productId;
    private Integer quantity;
    private String optionName;
    private Double price;

    public CartItemRequest() {
    }

    public CartItemRequest(Long productId, Integer quantity, String optionName, Double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.optionName = optionName;
        this.price = price;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

