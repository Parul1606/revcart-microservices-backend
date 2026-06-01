package com.revature.cartservice.service;

import com.revature.cartservice.client.ProductClient;
import com.revature.cartservice.dto.Product;
import com.revature.cartservice.entity.Wishlist;
import com.revature.cartservice.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductClient productClient;

    public WishlistService(WishlistRepository wishlistRepository, ProductClient productClient) {
        this.wishlistRepository = wishlistRepository;
        this.productClient = productClient;
    }

    public List<Product> getUserWishlist(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        return wishlists.stream()
                .map(wishlist -> productClient.getProductById(wishlist.getProductId()).getData())
                .collect(Collectors.toList());
    }

    public Wishlist addToWishlist(Long userId, Long productId) {
        // Check if already in wishlist
        if (wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new RuntimeException("Product already in wishlist");
        }

        // Verify product exists
        Product product = productClient.getProductById(productId).getData();
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setProductId(productId);
        return wishlistRepository.save(wishlist);
    }

    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }

    public void clearWishlist(Long userId) {
        wishlistRepository.deleteByUserId(userId);
    }
}
