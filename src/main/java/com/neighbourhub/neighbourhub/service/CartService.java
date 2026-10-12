package com.neighbourhub.neighbourhub.service;

import com.neighbourhub.neighbourhub.dto.AddToCartRequest;
import com.neighbourhub.neighbourhub.dto.CartItemResponse;
import com.neighbourhub.neighbourhub.dto.CartResponse;
import com.neighbourhub.neighbourhub.dto.UpdateCartItemRequest;
import com.neighbourhub.neighbourhub.entity.CartItem;
import com.neighbourhub.neighbourhub.entity.Product;
import com.neighbourhub.neighbourhub.entity.User;
import com.neighbourhub.neighbourhub.repository.CartItemRepository;
import com.neighbourhub.neighbourhub.repository.ProductRepository;
import com.neighbourhub.neighbourhub.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public CartResponse getCart(String email) {

        User user = getUser(email);

        List<CartItem> cartItems =
                cartItemRepository.findByUser(user);

        List<CartItemResponse> items =
                cartItems.stream()
                        .map(this::toResponse)
                        .toList();

        int totalItems = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        double totalAmount = cartItems.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice()
                                * item.getQuantity()
                )
                .sum();

        return new CartResponse(
                items,
                totalItems,
                totalAmount
        );
    }

    public CartResponse addToCart(
            AddToCartRequest request,
            String email
    ) {

        User user = getUser(email);

        Product product =
                productRepository.findById(
                        request.getProductId()
                ).orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found"
                        )
                );

        if (!Boolean.TRUE.equals(
                product.getAvailable()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This product is currently unavailable"
            );
        }

        int requestedQuantity =
                request.getQuantity();

        int stock =
                product.getStockQuantity() != null
                        ? product.getStockQuantity()
                        : 0;

        if (requestedQuantity > stock) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requested quantity exceeds available stock"
            );
        }

        CartItem cartItem =
                cartItemRepository
                        .findByUserIdAndProductId(
                                user.getId(),
                                product.getId()
                        )
                        .orElse(null);

        if (cartItem == null) {

            LocalDateTime now =
                    LocalDateTime.now();

            cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(requestedQuantity)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

        } else {

            int newQuantity =
                    cartItem.getQuantity()
                            + requestedQuantity;

            if (newQuantity > stock) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Total quantity exceeds available stock"
                );
            }

            cartItem.setQuantity(
                    newQuantity
            );

            cartItem.setUpdatedAt(
                    LocalDateTime.now()
            );
        }

        cartItemRepository.save(cartItem);

        return getCart(email);
    }

    public CartResponse updateCartItem(
            Long cartItemId,
            UpdateCartItemRequest request,
            String email
    ) {

        User user = getUser(email);

        CartItem cartItem =
                cartItemRepository
                        .findByIdAndUserId(
                                cartItemId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Cart item not found"
                                )
                        );

        Product product =
                cartItem.getProduct();

        if (!Boolean.TRUE.equals(
                product.getAvailable()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This product is currently unavailable"
            );
        }

        int stock =
                product.getStockQuantity() != null
                        ? product.getStockQuantity()
                        : 0;

        if (request.getQuantity() > stock) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requested quantity exceeds available stock"
            );
        }

        cartItem.setQuantity(
                request.getQuantity()
        );

        cartItem.setUpdatedAt(
                LocalDateTime.now()
        );

        cartItemRepository.save(cartItem);

        return getCart(email);
    }

    public CartResponse removeFromCart(
            Long cartItemId,
            String email
    ) {

        User user = getUser(email);

        CartItem cartItem =
                cartItemRepository
                        .findByIdAndUserId(
                                cartItemId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Cart item not found"
                                )
                        );

        cartItemRepository.delete(cartItem);

        return getCart(email);
    }

    public void clearCart(String email) {

        User user = getUser(email);

        cartItemRepository.deleteByUser(user);
    }

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );
    }

    private CartItemResponse toResponse(
            CartItem cartItem
    ) {

        Product product =
                cartItem.getProduct();

        Double subtotal =
                product.getPrice()
                        * cartItem.getQuantity();

        return new CartItemResponse(
                cartItem.getId(),
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                product.getPrice(),
                cartItem.getQuantity(),
                subtotal,
                product.getShop().getId(),
                product.getShop().getName(),
                product.getAvailable()
        );
    }
}