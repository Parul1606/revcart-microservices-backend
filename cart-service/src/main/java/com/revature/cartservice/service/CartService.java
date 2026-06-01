package com.revature.cartservice.service;

import com.revature.cartservice.client.ProductClient;
import com.revature.cartservice.dto.Product;
import com.revature.cartservice.entity.Cart;
import com.revature.cartservice.entity.CartItem;
import com.revature.cartservice.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;

    public CartService(CartRepository cartRepository, ProductClient productClient) {
        this.cartRepository = cartRepository;
        this.productClient = productClient;
    }

    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });
    }

    public Cart addItem(Long userId, Long productId, int quantity) {
        Cart cart = getCart(userId);

        // Verify product exists via Feign
        Product product = productClient.getProductById(productId).getData();
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            cart.addCartItem(newItem);
        }

        return cartRepository.save(cart);
    }

    public Cart updateItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = getCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        item.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    public Cart removeItem(Long userId, Long productId) {
        Cart cart = getCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cart.removeCartItem(item);
        return cartRepository.save(cart);
    }

    public Cart removeItemById(Long userId, Long itemId) {
        Cart cart = getCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cart.removeCartItem(item);
        return cartRepository.save(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = getCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
