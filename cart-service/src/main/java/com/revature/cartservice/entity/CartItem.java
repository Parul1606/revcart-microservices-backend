package com.revature.cartservice.entity;

import jakarta.persistence.*;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId; // Reference to Product Service

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Cart cart;

    public CartItem() {
    }

    public CartItem(Long id, Long productId, int quantity, Cart cart) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.cart = cart;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
