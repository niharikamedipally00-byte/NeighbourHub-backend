package com.neighbourhub.neighbourhub.repository;

import com.neighbourhub.neighbourhub.entity.Service;
import com.neighbourhub.neighbourhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByProvider(User provider);

    List<Service> findByCategoryIgnoreCase(String category);

    List<Service> findByAvailableTrue();

    List<Service> findByNameContainingIgnoreCase(String name);

    List<Service> findByProviderId(Long providerId);
}