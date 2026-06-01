package com.revature.cartservice.dto;

import java.util.List;

public class CartResponse {
    private Long id;
    private Long userId;
    private List<CartItemResponse> items;

    public CartResponse() {
    }

    public CartResponse(Long id, Long userId, List<CartItemResponse> items) {
        this.id = id;
        this.userId = userId;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }
}
