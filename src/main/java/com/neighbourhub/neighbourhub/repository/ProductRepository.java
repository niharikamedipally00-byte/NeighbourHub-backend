package com.neighbourhub.neighbourhub.repository;

import com.neighbourhub.neighbourhub.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findByShopId(Long shopId);

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByAvailableTrue();
}