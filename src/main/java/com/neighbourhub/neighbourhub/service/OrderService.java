package com.neighbourhub.neighbourhub.service;

import com.neighbourhub.neighbourhub.dto.CreateOrderRequest;
import com.neighbourhub.neighbourhub.dto.OrderItemResponse;
import com.neighbourhub.neighbourhub.dto.OrderResponse;
import com.neighbourhub.neighbourhub.entity.CartItem;
import com.neighbourhub.neighbourhub.entity.Order;
import com.neighbourhub.neighbourhub.entity.OrderItem;
import com.neighbourhub.neighbourhub.entity.OrderStatus;
import com.neighbourhub.neighbourhub.entity.Product;
import com.neighbourhub.neighbourhub.entity.Shop;
import com.neighbourhub.neighbourhub.entity.User;
import com.neighbourhub.neighbourhub.repository.CartItemRepository;
import com.neighbourhub.neighbourhub.repository.OrderItemRepository;
import com.neighbourhub.neighbourhub.repository.OrderRepository;
import com.neighbourhub.neighbourhub.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse createOrder(
            CreateOrderRequest request,
            String email
    ) {
        User user = getUser(email);

        if (request.getDeliveryAddress() == null
                || request.getDeliveryAddress().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Delivery address is required"
            );
        }

        List<CartItem> cartItems =
                cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Your cart is empty"
            );
        }

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            if (!Boolean.TRUE.equals(product.getAvailable())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        product.getName()
                                + " is currently unavailable"
                );
            }

            if (product.getStockQuantity() == null
                    || product.getStockQuantity()
                    < cartItem.getQuantity()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient stock for "
                                + product.getName()
                );
            }
        }

        double totalAmount = cartItems.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice()
                                * item.getQuantity()
                )
                .sum();

        LocalDateTime now = LocalDateTime.now();

        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.PLACED)
                .deliveryAddress(
                        request.getDeliveryAddress().trim()
                )
                .phone(request.getPhone())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Order savedOrder =
                orderRepository.save(order);

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();
            Shop shop = product.getShop();

            double subtotal =
                    product.getPrice()
                            * cartItem.getQuantity();

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .shop(shop)
                    .shopName(shop.getName())
                    .build();

            orderItemRepository.save(orderItem);

            product.setStockQuantity(
                    product.getStockQuantity()
                            - cartItem.getQuantity()
            );

            if (product.getStockQuantity() == 0) {
                product.setAvailable(false);
            }
        }

        List<OrderItemResponse> items =
                cartItems.stream()
                        .map(this::toItemResponse)
                        .toList();

        cartItemRepository.deleteByUser(user);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                savedOrder.getDeliveryAddress(),
                savedOrder.getPhone(),
                savedOrder.getCreatedAt(),
                items
        );
    }

    public List<OrderResponse> getMyOrders(
            String email
    ) {
        User user = getUser(email);

        return orderRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getMyOrder(
            Long orderId,
            String email
    ) {
        User user = getUser(email);

        Order order =
                orderRepository
                        .findByIdAndUserId(
                                orderId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Order not found"
                                )
                        );

        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(
            Long orderId,
            String email
    ) {
        User user = getUser(email);

        Order order =
                orderRepository
                        .findByIdAndUserId(
                                orderId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Order not found"
                                )
                        );

        if (order.getStatus() != OrderStatus.PLACED
                && order.getStatus()
                != OrderStatus.CONFIRMED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This order can no longer be cancelled"
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        return toResponse(
                orderRepository.save(order)
        );
    }

    private OrderResponse toResponse(
            Order order
    ) {
        List<OrderItemResponse> items =
                orderItemRepository
                        .findByOrderId(order.getId())
                        .stream()
                        .map(item ->
                                new OrderItemResponse(
                                        item.getProduct().getId(),
                                        item.getProductName(),
                                        item.getPrice(),
                                        item.getQuantity(),
                                        item.getSubtotal(),
                                        item.getShop().getId(),
                                        item.getShopName()
                                )
                        )
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getDeliveryAddress(),
                order.getPhone(),
                order.getCreatedAt(),
                items
        );
    }

    private OrderItemResponse toItemResponse(
            CartItem item
    ) {
        Product product = item.getProduct();
        Shop shop = product.getShop();

        double subtotal =
                product.getPrice()
                        * item.getQuantity();

        return new OrderItemResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                item.getQuantity(),
                subtotal,
                shop.getId(),
                shop.getName()
        );
    }

    private User getUser(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );
    }
}