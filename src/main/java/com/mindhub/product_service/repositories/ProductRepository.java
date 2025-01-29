package com.mindhub.product_service.repositories;

import com.mindhub.product_service.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByName(String name);
    List<Product> findByDescription(String description);
    List<Product> findByPrice(double price);
    List<Product> findByStock(int stock);

    boolean existsById(long id);
    boolean existsByName(String name);
    boolean existsByDescription(String description);

    int countByName(String name);
    int countByPrice(double price);

    // Pagination example
    Page<Product> findByName(String name, Pageable pageable);
    Page<Product> findByPrice(double price, Pageable pageable);
}
