package com.mindhub.product_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindhub.product_service.dtos.ProductDTO;
import com.mindhub.product_service.dtos.UpdateProductDTO;
import com.mindhub.product_service.models.Product;
import com.mindhub.product_service.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// This annotation is used to test Spring MVC controllers,
// focusing only on the web layer
@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    // Autowired to inject MockMvc for simulating HTTP requests
    @Autowired
    private MockMvc mockMvc;
    // MockBean to mock the dependency
    @MockBean
    private ProductService productService;
    // Autowired to inject ObjectMapper for JSON serialization/deserialization
    @Autowired
    private ObjectMapper objectMapper;
    // Test product DTO object to be used in tests
    private ProductDTO testProduct;
    // This method runs before each test to set up initial data
    @BeforeEach
    void setUp() {
        // Create a test product and its DTO
        Product product = new Product("Cheese", "kg", 10.0, 10);
        testProduct = new ProductDTO(product);
    }
    // Test to verify that the /api/product/ endpoint returns all the products
    @Test
    void getAllProductsShouldReturnProducts() throws Exception {
        // Mock the service to return a list of products
        List<ProductDTO> products = Collections.singletonList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testProduct.getId()))
                .andExpect(jsonPath("$[0].name").value(testProduct.getName()))
                .andExpect(jsonPath("$[0].description").value(testProduct.getDescription()))
                .andExpect(jsonPath("$[0].price").value(testProduct.getPrice()))
                .andExpect(jsonPath("$[0].stock").value(testProduct.getStock()));
    }

    @Test
    void updateProductShouldUpdateProduct() throws Exception {
        // Create a DTO with updated data
        UpdateProductDTO updateDto = new UpdateProductDTO("American Cheese", "kg", 20.0, 5);
        // Mock the service to return the test product and confirm the update
        when(productService.getProductDTOById(testProduct.getId())).thenReturn(testProduct);
        when(productService.updateProduct(anyLong(), any(UpdateProductDTO.class))).thenReturn(true);

        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON) // Set content type to JSON
                        .content(objectMapper.writeValueAsString(updateDto))) // Convert DTO to JSON
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(content().string("Product updated successfully")); // Expected message
    }
}
