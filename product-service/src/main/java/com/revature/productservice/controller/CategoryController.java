package com.revature.productservice.controller;

import com.revature.common.dto.ApiResponse;
import com.revature.productservice.entity.Product;
import com.revature.productservice.repository.ProductRepository;
import com.revature.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    public CategoryController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = productService.getUniqueCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{category}/products")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productRepository.findByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}
