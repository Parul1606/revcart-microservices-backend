package com.revature.cartservice.client;

import com.revature.cartservice.dto.Product;
import com.revature.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ApiResponse<Product> getProductById(@PathVariable("id") Long id);
}
