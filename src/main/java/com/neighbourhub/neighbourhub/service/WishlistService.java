package com.neighbourhub.neighbourhub.service;

import com.neighbourhub.neighbourhub.dto.WishlistResponse;
import com.neighbourhub.neighbourhub.entity.Product;
import com.neighbourhub.neighbourhub.entity.User;
import com.neighbourhub.neighbourhub.entity.Wishlist;
import com.neighbourhub.neighbourhub.repository.ProductRepository;
import com.neighbourhub.neighbourhub.repository.UserRepository;
import com.neighbourhub.neighbourhub.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistResponse addToWishlist(
            Long userId,
            Long productId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        Product product = productRepository.findById(productId)
                .orElseThrow(
                        () -> new RuntimeException("Product not found")
                );

        Wishlist existing =
                wishlistRepository
                        .findByUserIdAndProductId(
                                userId,
                                productId
                        )
                        .orElse(null);

        if (existing != null) {
            return toResponse(existing);
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(
                wishlistRepository.save(wishlist)
        );
    }

    public List<WishlistResponse> getWishlist(
            Long userId
    ) {

        return wishlistRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public boolean isWishlisted(
            Long userId,
            Long productId
    ) {

        return wishlistRepository
                .existsByUserIdAndProductId(
                        userId,
                        productId
                );
    }

    public void removeFromWishlist(
            Long userId,
            Long productId
    ) {

        wishlistRepository.deleteByUserIdAndProductId(
                userId,
                productId
        );
    }

    private WishlistResponse toResponse(
            Wishlist wishlist
    ) {

        Product product = wishlist.getProduct();

        return WishlistResponse.builder()
                .id(wishlist.getId())

                .productId(product.getId())
                .productName(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .available(product.getAvailable())
                .stockQuantity(product.getStockQuantity())

                .shopId(
                        product.getShop() != null
                                ? product.getShop().getId()
                                : null
                )

                .shopName(
                        product.getShop() != null
                                ? product.getShop().getName()
                                : null
                )

                .addedAt(
                        wishlist.getCreatedAt().toString()
                )

                .build();
    }
}