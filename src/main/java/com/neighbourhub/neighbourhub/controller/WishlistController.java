package com.neighbourhub.neighbourhub.controller;

import com.neighbourhub.neighbourhub.dto.WishlistResponse;
import com.neighbourhub.neighbourhub.service.WishlistService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{userId}/{productId}")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                wishlistService.addToWishlist(
                        userId,
                        productId
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<WishlistResponse>> getWishlist(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                wishlistService.getWishlist(userId)
        );
    }

    @GetMapping("/{userId}/check/{productId}")
    public ResponseEntity<Boolean> isWishlisted(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                wishlistService.isWishlisted(
                        userId,
                        productId
                )
        );
    }

    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<String> removeFromWishlist(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {

        wishlistService.removeFromWishlist(
                userId,
                productId
        );

        return ResponseEntity.ok(
                "Product removed from wishlist"
        );
    }
}