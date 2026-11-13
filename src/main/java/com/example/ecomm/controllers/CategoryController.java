package com.example.ecomm.controllers;

import com.example.ecomm.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories/{name}/average-price")
    public ResponseEntity<Double> getAveragePrice(@PathVariable("name") String name) {
        Double average = categoryService.getAveragePrice(name);
        return average == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(average);
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return categoryService.getCategoryReport();
    }
}
