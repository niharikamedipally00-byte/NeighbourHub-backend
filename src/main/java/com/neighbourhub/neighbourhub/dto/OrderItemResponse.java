package com.neighbourhub.neighbourhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemResponse {

    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double subtotal;
    private Long shopId;
    private String shopName;
}