package com.example.ecomm.mappers;

import com.example.ecomm.dtos.CategoryDto;
import com.example.ecomm.dtos.ProductDto;
import com.example.ecomm.models.Category;
import com.example.ecomm.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private ProductMapper mapper;

    ProductMapperTest() {
        mapper = new ProductMapper();
    }

    @Test
    @DisplayName("toDto copies the fields and the category")
    void toDto_product_copiesFieldsAndCategory() {
        Category category = new Category();
        category.setName("beauty");
        Product product = new Product();
        product.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        product.setName("Mascara");
        product.setPrice(new BigDecimal("9.99"));
        product.setCategory(category);

        ProductDto dto = mapper.toDto(product);

        assertThat(dto.getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertThat(dto.getName()).isEqualTo("Mascara");
        assertThat(dto.getPrice()).isEqualTo(new BigDecimal("9.99"));
        assertThat(dto.getCategory().getName()).isEqualTo("beauty");
    }

    @Test
    @DisplayName("toDto keeps the category null when the product has none")
    void toDto_noCategory_keepsCategoryNull() {
        Product product = new Product();
        product.setName("Mascara");

        ProductDto dto = mapper.toDto(product);

        assertThat(dto.getCategory()).isNull();
    }

    @Test
    @DisplayName("toDto returns null for a null product")
    void toDto_nullProduct_returnsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    @DisplayName("toEntity copies the fields and the category")
    void toEntity_dto_copiesFieldsAndCategory() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("home");
        ProductDto dto = new ProductDto();
        dto.setName("Desk Lamp");
        dto.setPrice(new BigDecimal("499.50"));
        dto.setCategory(categoryDto);

        Product product = mapper.toEntity(dto);

        assertThat(product.getName()).isEqualTo("Desk Lamp");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("499.50"));
        assertThat(product.getCategory().getName()).isEqualTo("home");
    }
}
