package com.example.ecomm.services;

import com.example.ecomm.models.State;
import com.example.ecomm.repository.CategoryRepository;
import com.example.ecomm.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public CategoryService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Double getAveragePrice(String categoryName) {
        return productRepository.findAveragePriceInCategory(categoryName, State.ACTIVE);
    }

    public List<String> getCategoryReport() {
        return categoryRepository.findProductCounts().stream()
                .map(category -> category.getName() + ": " + category.getProductCount())
                .toList();
    }
}
