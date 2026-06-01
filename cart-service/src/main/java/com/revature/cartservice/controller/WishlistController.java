package com.revature.cartservice.controller;

import com.revature.cartservice.dto.Product;
import com.revature.cartservice.entity.Wishlist;
import com.revature.cartservice.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getWishlist(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        List<Product> wishlist = wishlistService.getUserWishlist(userId);
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> addToWishlist(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @PathVariable Long productId) {
        try {
            Wishlist wishlist = wishlistService.addToWishlist(userId, productId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product added to wishlist");
            response.put("wishlistId", wishlist.getId());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> removeFromWishlist(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @PathVariable Long productId) {
        wishlistService.removeFromWishlist(userId, productId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Product removed from wishlist");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkWishlist(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @PathVariable Long productId) {
        boolean inWishlist = wishlistService.isInWishlist(userId, productId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("inWishlist", inWishlist);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearWishlist(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        wishlistService.clearWishlist(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Wishlist cleared");

        return ResponseEntity.ok(response);
    }
}
