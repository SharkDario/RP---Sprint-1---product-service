package com.mindhub.product_service.services.impl;

import com.mindhub.product_service.dtos.NewProductDTO;
import com.mindhub.product_service.dtos.ProductDTO;
import com.mindhub.product_service.dtos.UpdateProductDTO;
import com.mindhub.product_service.models.Product;
import com.mindhub.product_service.repositories.ProductRepository;
import com.mindhub.product_service.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductDTO getProductDTOById(Long id) {
        return new ProductDTO(getProductById(id));
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product with ID " + id + " not found"));
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void createProduct(NewProductDTO newProductDTO) {
        // Create product
        Product product = new Product(newProductDTO.name(), newProductDTO.description(), newProductDTO.price(), newProductDTO.stock());
        // Save product
        saveProduct(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByName(String name) {
        List<Product> products = productRepository.findByName(name);
        return products.stream().map(ProductDTO::new).collect(Collectors.toList());
    }

    @Override
    public boolean updateProduct(Long id, UpdateProductDTO updateProductDTO) {
        Product product = getProductById(id);
        // Update
        product.setName(updateProductDTO.name());
        product.setDescription(updateProductDTO.description());
        product.setPrice(updateProductDTO.price());
        product.setStock(updateProductDTO.stock());
        saveProduct(product);
        return true;
    }

    @Override
    public boolean deleteProduct(Long id) {
        if(!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }
}
