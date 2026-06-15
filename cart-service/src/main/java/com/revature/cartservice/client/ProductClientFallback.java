package com.revature.cartservice.client;

import com.revature.cartservice.dto.Product;
import com.revature.common.dto.ApiResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public ApiResponse<Product> getProductById(Long id) {
        Product mockProduct = new Product();
        mockProduct.setId(id);
        mockProduct.setName("Item Temporarily Unavailable");
        mockProduct.setDescription("We are having trouble fetching product details. You can still manage this item in your cart.");
        mockProduct.setPrice(0.0);
        mockProduct.setImageUrl("");
        mockProduct.setStock(0);
        mockProduct.setCategoryId(1L);
        
        return ApiResponse.success("Fallback active: Product service is offline.", mockProduct);
    }
}
