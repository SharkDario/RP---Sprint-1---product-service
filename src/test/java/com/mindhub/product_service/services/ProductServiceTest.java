package com.mindhub.product_service.services;

import com.mindhub.product_service.models.Product;
import com.mindhub.product_service.repositories.ProductRepository;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

// Use @SpringBootTest to load the full Spring context and verify component integration
@SpringBootTest
@ActiveProfiles("test")
public class ProductServiceTest {
    // Allows for creating and managing mocks of dependencies in unit tests,
    // facilitating the simulation of external components.
    // Simulacrum
    @MockBean
    private ProductRepository productRepository;
    // Use @MockBean to replace real beans with mocks during testing, allowing you to focus on specific interactions.
    @Autowired
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        //testProduct = new Product("Miguel7", encodedPassword, "miguel@gmail.com");
        testProduct = spy(new Product("Cheese", "kg", 10.0, 10));
        when(testProduct.getId()).thenReturn(1L);
        // Mock the repository to return the test user when findByEmail is called
        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(productRepository.existsById(testProduct.getId())).thenReturn(true);
    }

    @Test
    public void testGetProductById() {
        // Mock the repository to return the test user when findById is called
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Call the service method
        Product result = productService.getProductById(1L);

        // Verify the result
        assertNotNull(result);
        assertEquals("Cheese", result.getName());
        assertEquals("kg", result.getDescription());

        // Verify that the repository method was called
        verify(productRepository, times(1)).findById(eq(1L));
    }

    @Test
    public void testSaveProduct() {
        // Mock the repository to return the test user when save is called
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Call the service method
        Product result = productService.saveProduct(testProduct);

        // Verify the result
        assertNotNull(result);
        assertEquals("Cheese", result.getName());
        assertEquals("kg", result.getDescription());

        // Verify that the repository method was called
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    public void testDeleteProduct() {
        // Mock the repository to return true when checking for existing user
        when(productRepository.existsById(1l)).thenReturn(true);

        assertTrue(productRepository.existsById(1L));
        // Call the service method
        boolean result = productService.deleteProduct(1L);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteProductNotFound() {
        // Mock the repository to return false when checking for existing user
        when(productRepository.existsById(eq(2L))).thenReturn(false);

        // Call the service method
        boolean result = productService.deleteProduct(eq(2L));

        // Verify the result
        assertFalse(result);
    }
}
