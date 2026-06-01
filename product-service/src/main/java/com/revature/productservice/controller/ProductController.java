package com.revature.productservice.controller;

import com.revature.common.dto.ApiResponse;
import com.revature.productservice.entity.Product;
import com.revature.productservice.repository.ProductRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productRepository.findByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> searchProducts(@RequestParam String query) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(query);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        List<String> categories = productRepository.findAll().stream()
                .map(Product::getCategory)
                .distinct()
                .sorted()
                .toList();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<Product>>> getTrendingProducts(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        List<Product> allProducts = productRepository.findAll();
        List<Product> trending = allProducts.stream()
                .filter(p -> p.getRating() != null && p.getRating() > 4.0)
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
                .toList();

        int fromIndex = Math.max(page, 0) * Math.max(size, 1);
        int toIndex = Math.min(fromIndex + Math.max(size, 1), trending.size());
        if (fromIndex > toIndex)
            fromIndex = toIndex;

        return ResponseEntity.ok(ApiResponse.success(trending.subList(fromIndex, toIndex)));
    }
}
