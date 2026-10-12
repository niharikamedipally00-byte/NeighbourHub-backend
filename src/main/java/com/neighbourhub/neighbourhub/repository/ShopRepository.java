package com.neighbourhub.neighbourhub.repository;

import com.neighbourhub.neighbourhub.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    List<Shop> findByCategoryIgnoreCase(String category);

    List<Shop> findByCityIgnoreCase(String city);

    List<Shop> findByNameContainingIgnoreCase(String name);
}