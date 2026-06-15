package com.revature.cartservice.dto;

public class CartItemResponse {
    private Long id;
    private Product product;
    private int quantity;
    private String optionName;
    private Double price;

    public CartItemResponse() {
    }

    public CartItemResponse(Long id, Product product, int quantity, String optionName, Double price) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.optionName = optionName;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
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

