package com.revature.cartservice.controller;

import com.revature.cartservice.client.ProductClient;
import com.revature.cartservice.dto.CartItemRequest;
import com.revature.cartservice.dto.CartItemResponse;
import com.revature.cartservice.dto.CartResponse;
import com.revature.cartservice.dto.Product;
import com.revature.cartservice.entity.Cart;
import com.revature.cartservice.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final ProductClient productClient;

    public CartController(CartService cartService, ProductClient productClient) {
        this.cartService = cartService;
        this.productClient = productClient;
    }

    private CartResponse toCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setItems(cart.getItems().stream()
                .map(item -> {
                    CartItemResponse itemResponse = new CartItemResponse();
                    itemResponse.setId(item.getId());
                    // Fetch product details via Feign and unwrap ApiResponse
                    Product product = productClient.getProductById(item.getProductId()).getData();
                    itemResponse.setProduct(product);
                    itemResponse.setQuantity(item.getQuantity());
                    return itemResponse;
                })
                .collect(Collectors.toList()));
        return response;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        return ResponseEntity.ok(toCartResponse(cartService.getCart(userId)));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @RequestBody CartItemRequest request) {
        return ResponseEntity
                .ok(toCartResponse(cartService.addItem(userId, request.getProductId(), request.getQuantity())));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> payload) {
        return ResponseEntity
                .ok(toCartResponse(cartService.updateItemQuantity(userId, productId, payload.get("quantity"))));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(toCartResponse(cartService.removeItemById(userId, itemId)));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}
