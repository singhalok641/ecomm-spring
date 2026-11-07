package com.example.ecomm.services;

import com.example.ecomm.dtos.ProductPatchDto;
import com.example.ecomm.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IProductService {
    Product getProductById(UUID id);
    Page<Product> getAllProducts(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Product createProduct(Product product);
    Product replaceProduct(Product product, UUID id);
    Product patchProduct(ProductPatchDto patch, UUID id);
    boolean deleteProduct(UUID id);
}
