package com.example.ecomm.services;

import com.example.ecomm.mappers.ProductMapper;
import com.example.ecomm.models.Product;
import com.example.ecomm.models.State;
import com.example.ecomm.repository.CategoryRepository;
import com.example.ecomm.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StorageProductServiceTest {
    @Mock private ProductRepository productRepo;
    @Mock private CategoryRepository categoryRepo;

    @InjectMocks
    private StorageProductService storageProductService;

    @Test
    void createProduct_clientSendsAnId_savesWithoutItAndWithDates() {
        Product input = new Product();
        input.setId(UUID.randomUUID());
        input.setName("Mascara");

        when(productRepo.save(any(Product.class))).thenAnswer(call -> call.getArgument(0));

        Product result = storageProductService.createProduct(input);
        assertThat(result.getName()).isEqualTo("Mascara");

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getCreatedAt()).isNotNull();
        assertThat(captor.getValue().getLastUpdatedAt()).isNotNull();
    }

//    @Test
//    void searchProducts_onlyMinPrice_filtersByMinPrice() {
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // Act: Customer sends minPrice = 100, but maxPrice = null (no upper limit)
//        storageProductService.searchProducts(new BigDecimal("100"), null, pageable);
//
//        // Assert: Check productRepo's clipboard — did the service call the price-filter method?
//        verify(productRepo).findByPriceBetweenAndState(
//                eq(new BigDecimal("100")),
//                any(BigDecimal.class),
//                eq(State.ACTIVE),
//                eq(pageable)
//        );
//    }
}
