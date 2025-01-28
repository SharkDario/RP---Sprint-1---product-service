package com.mindhub.product_service.services;

import com.mindhub.product_service.dtos.*;
import com.mindhub.product_service.models.Product;

import java.util.List;

public interface ProductService {
    ProductDTO getProductDTOById(Long id);

    Product getProductById(Long id);

    Product saveProduct(Product product);

    void createProduct(NewProductDTO newProductDTO);

    public List<ProductDTO> getAllProducts();

    public List<ProductDTO> getProductsByName(String name);

    boolean updateProduct(Long id, UpdateProductDTO updateProductDTO);

    public boolean deleteProduct(Long id);

    public boolean existsById(Long id);

    boolean existsProductById(Long id);

    List<ExistentProductsRecord> getAllAvailableProducts(List<ProductQuantityRecord> productQuantityRecordList);
}