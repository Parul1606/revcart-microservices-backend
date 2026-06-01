package com.revature.productservice.controller;

import com.revature.common.dto.ApiResponse;
import com.revature.productservice.entity.Product;
import com.revature.productservice.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ProductRepository productRepository;

    public CategoryController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = productRepository.findAll().stream()
                .map(Product::getCategory)
                .distinct()
                .sorted()
                .toList();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{category}/products")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productRepository.findByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}
