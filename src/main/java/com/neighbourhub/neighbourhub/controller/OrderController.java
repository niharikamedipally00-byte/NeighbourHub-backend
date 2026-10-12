package com.neighbourhub.neighbourhub.controller;

import com.neighbourhub.neighbourhub.dto.CreateOrderRequest;
import com.neighbourhub.neighbourhub.dto.OrderResponse;
import com.neighbourhub.neighbourhub.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                orderService.createOrder(
                        request,
                        authentication.getName()
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                orderService.getMyOrders(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getMyOrder(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                orderService.getMyOrder(
                        orderId,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                orderService.cancelOrder(
                        orderId,
                        authentication.getName()
                )
        );
    }
}