package com.revature.productservice.service;

import com.revature.productservice.entity.Product;
import com.revature.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findAll().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setCategory(updatedProduct.getCategory());
        existing.setBrand(updatedProduct.getBrand());
        existing.setStock(updatedProduct.getStock());
        existing.setImageUrl(updatedProduct.getImageUrl());
        existing.setRating(updatedProduct.getRating());

        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findAll().stream()
                .filter(p -> p.getStock() != null && p.getStock() < threshold)
                .collect(Collectors.toList());
    }

    public Product updateStock(Long id, Integer quantity) {
        Product product = getProductById(id);
        product.setStock(quantity);
        return productRepository.save(product);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        (p.getDescription() != null
                                && p.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
    }
}
