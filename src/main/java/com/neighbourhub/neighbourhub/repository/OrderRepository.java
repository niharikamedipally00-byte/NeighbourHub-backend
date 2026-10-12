package com.neighbourhub.neighbourhub.repository;

import com.neighbourhub.neighbourhub.entity.Order;
import com.neighbourhub.neighbourhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    Optional<Order> findByIdAndUserId(
            Long id,
            Long userId
    );
}