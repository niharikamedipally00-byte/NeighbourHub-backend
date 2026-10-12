package com.neighbourhub.neighbourhub.controller;

import com.neighbourhub.neighbourhub.dto.AddToCartRequest;
import com.neighbourhub.neighbourhub.dto.CartResponse;
import com.neighbourhub.neighbourhub.dto.UpdateCartItemRequest;
import com.neighbourhub.neighbourhub.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cartService.getCart(
                        authentication.getName()
                )
        );
    }

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cartService.addToCart(
                        request,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cartService.updateCartItem(
                        cartItemId,
                        request,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cartService.removeFromCart(
                        cartItemId,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart(
            Authentication authentication
    ) {
        cartService.clearCart(
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Cart cleared successfully"
        );
    }
}