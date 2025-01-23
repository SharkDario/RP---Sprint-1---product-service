package com.mindhub.product_service.repositories;

import com.mindhub.product_service.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// This annotation is used for JPA tests, it configures an in-memory database and JPA repositories
@DataJpaTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    public void setUp(){
        // Create a product
        product = new Product();
        product.setName("Cheese");
        product.setDescription("kg");
        product.setPrice(10.0);
        product.setStock(10);
        productRepository.save(product);
    }

    @Test
    public void testCreateNewProduct() {
        Product newproduct = new Product();
        newproduct.setName("Bread");
        newproduct.setDescription("kg");
        newproduct.setPrice(7.0);
        newproduct.setStock(10);
        Product productToSave = productRepository.save(newproduct);
        assertNotNull(productToSave.getId());
        assertEquals("Bread", productToSave.getName());
    }

    @Test
    public void testUpdateProduct() {
        Product foundproduct = productRepository.findById(product.getId()).orElse(null);
        assertThat(foundproduct).isNotNull();

        foundproduct.setName("American Cheese");
        productRepository.save(foundproduct);

        Product updatedproduct = productRepository.findById(product.getId()).orElse(null);
        assertThat(updatedproduct).isNotNull();
        assertThat(updatedproduct.getName()).isEqualTo("American Cheese");
    }

    @Test
    public void testDeleteProduct() {
        productRepository.deleteById(product.getId());
        boolean exists = productRepository.existsById(product.getId());
        assertFalse(exists);
    }
}
