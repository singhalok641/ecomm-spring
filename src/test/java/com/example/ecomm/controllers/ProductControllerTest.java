package com.example.ecomm.controllers;

import com.example.ecomm.clients.AuthClient;
import com.example.ecomm.mappers.ProductMapper;
import com.example.ecomm.models.Product;
import com.example.ecomm.services.IProductService;
import com.example.ecomm.services.PitchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(ProductMapper.class)
public class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    // Explicit names: the web slice has no IProductService beans, so without a name the mock gets an
    // auto-generated one and the controller's @Qualifier("...") lookups fail.
    @MockitoBean("storageProductService") private IProductService storageProductService;
    @MockitoBean("dummyJsonProductService") private IProductService dummyJsonProductService;
    @MockitoBean private PitchService pitchService;
    // TokenCheckFilter is part of the web slice and needs its collaborator.
    @MockitoBean private AuthClient authClient;

    @Test
    void getProductById_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/products/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

        // mockMvc.perform(get(...))
    }

    @Test
    void getProductById_existingProduct_returns200AndTheJson() throws Exception {
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(id);
        product.setName("Mascara");
        product.setPrice(new BigDecimal("9.99"));

        // stub
        when(storageProductService.getProductById(id)).thenReturn(product);

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Mascara"))
                .andExpect(jsonPath("$.price").value(9.99));
    }

    @Test
    void createProduct_noToken_returns401AndNothingIsSaved() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Mascara\"}"))
                .andExpect(status().isUnauthorized());

        verify(storageProductService, never()).createProduct(any());
    }
}
