package com.mindhub.product_service.services.impl;

import com.mindhub.product_service.dtos.*;
import com.mindhub.product_service.exceptions.ProductException;
import com.mindhub.product_service.models.Product;
import com.mindhub.product_service.repositories.ProductRepository;
import com.mindhub.product_service.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Override
    public boolean existsProductById(Long id) {
        return productRepository.existsById(id);
    }

    /*
    @Override
    public List<ExistentProductsRecord> getAllAvailableProducts(List<ProductQuantityRecord> productQuantityRecordList) {
        List<ExistentProductsRecord> listOfProducts = new ArrayList<>();
        productQuantityRecordList.forEach( product -> {
            if (existsProductById(product.id())){
                try {
                    Product realProduct = getProductById(product.id());
                    if (realProduct.getStock()>=product.quantity()){
                        listOfProducts.add(new ExistentProductsRecord(product.id(), realProduct.getPrice(), product.quantity()));
                        realProduct.setStock(realProduct.getStock() - product.quantity());
                        productRepository.save(realProduct);
                    } else {
                        listOfProducts.add(new ExistentProductsRecord(product.id(), null, product.quantity()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return listOfProducts;
    }

     */

    @Override
    public HashMap<Long, Integer> getAllAvailableProducts(List<ProductQuantityRecord> productQuantityRecordList){
        HashMap<Long, Integer> availableProductMap = new HashMap<>();

        productQuantityRecordList.forEach( product -> {
            try{
                Product aux = getProductById(product.id());
                availableProductMap.put(aux.getId(), aux.getStock());
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        return availableProductMap;
    }

    @Override
    public List<NewProductDTO> getAllDetailsProducts(List<ProductQuantityRecord> productQuantityRecordList){
        List<NewProductDTO> productsDTOs = new ArrayList<>();

        productQuantityRecordList.forEach( product -> {
            try{
                Product aux = getProductById(product.id());
                productsDTOs.add(new NewProductDTO(aux.getName(), aux.getDescription(), aux.getPrice(), product.quantity()));
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        return productsDTOs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductQuantity(Long idProduct, Integer quantity) throws ProductException {
        Product product = getProductById(idProduct);
        if (product.getStock()+quantity<0){
            throw new ProductException("Not enough stock", HttpStatus.NOT_ACCEPTABLE);
        }

        product.setStock(product.getStock()+quantity);
        productRepository.save(product);

    }
    public void updateProductsQuantity(List<ProductQuantityRecord> quantityRecord){
        quantityRecord.forEach(product ->{
            try {
                updateProductQuantity(product.id(), product.quantity());
            } catch (ProductException e) {
            }
        });
    }
}
