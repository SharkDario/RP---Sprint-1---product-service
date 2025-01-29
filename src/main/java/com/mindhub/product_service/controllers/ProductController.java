package com.mindhub.product_service.controllers;

import com.mindhub.product_service.dtos.*;
import com.mindhub.product_service.exceptions.ProductException;
import com.mindhub.product_service.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    // Dependencies Injection - Only things that are in the context of Spring Boot (has to be Component)
    // From behind generates a constructor and injects the bean for this repository (interface)
    @Autowired
    private ProductService productService; // inject the interface directly

    // Validate errors
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
    // Validate business exceptions
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public Map<String, String> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }

    // Validate general exceptions
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, String> handleGeneralExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred: " + ex.getMessage());
        return error;
    }

    // Endpoint to verify if a productId exists
    @GetMapping("/exists/{productId}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long productId) {
        boolean exists = productService.existsById(productId);
        return ResponseEntity.ok(exists);
    }

    // GET /products: Get all products.
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // POST /products: Create a product.
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody NewProductDTO newProductDTO) {
        productService.createProduct(newProductDTO);
        return new ResponseEntity<>("Product created successfully", HttpStatus.CREATED);
    }

    // PUT /products/{id}: Update the stock of a product.
    // PATCH: Update name, description, price, stock
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductDTO updateProductDTO) {
        try {
            productService.updateProduct(id, updateProductDTO);
            return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // DELETE /products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<HashMap<Long, Integer>> existsProducts(@RequestBody List<ProductQuantityRecord> recordList){
        HashMap<Long, Integer> products = productService.getAllAvailableProducts(recordList);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/details")
    public ResponseEntity<List<NewProductDTO>> detailsProducts(@RequestBody List<ProductQuantityRecord> recordList){
        List<NewProductDTO> products = productService.getAllDetailsProducts(recordList);
        return ResponseEntity.ok(products);
    }

    /*
    @PutMapping
    public ResponseEntity<List<ExistentProductsRecord>> existsProducts(@RequestBody List<ProductQuantityRecord> recordList){
        List<ExistentProductsRecord> products = productService.getAllAvailableProducts(recordList);
        return ResponseEntity.ok(products);
    }

     */

    @PutMapping("/to-order")
    public ResponseEntity<String> existProduct(@RequestBody List<ProductQuantityRecord> quantityRecord) throws ProductException {
        productService.updateProductsQuantity(quantityRecord);
        return new ResponseEntity<String>("The product/s were been updated successfully", HttpStatus.OK);
    }
}
