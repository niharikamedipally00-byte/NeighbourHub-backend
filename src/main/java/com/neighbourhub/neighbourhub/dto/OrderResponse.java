package com.neighbourhub.neighbourhub.dto;

import com.neighbourhub.neighbourhub.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private Double totalAmount;
    private OrderStatus status;
    private String deliveryAddress;
    private String phone;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}