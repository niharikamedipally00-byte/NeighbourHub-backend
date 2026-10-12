package com.neighbourhub.neighbourhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemResponse {

    private Long id;

    private Long productId;

    private String productName;

    private String productImageUrl;

    private Double price;

    private Integer quantity;

    private Double subtotal;

    private Long shopId;

    private String shopName;

    private Boolean available;
}