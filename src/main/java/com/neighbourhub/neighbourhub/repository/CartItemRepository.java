package com.neighbourhub.neighbourhub.repository;

import com.neighbourhub.neighbourhub.entity.CartItem;
import com.neighbourhub.neighbourhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserIdAndProductId(
            Long userId,
            Long productId
    );

    Optional<CartItem> findByIdAndUserId(
            Long id,
            Long userId
    );

    void deleteByUser(User user);
}