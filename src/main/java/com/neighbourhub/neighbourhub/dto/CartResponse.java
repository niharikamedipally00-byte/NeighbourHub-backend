package com.neighbourhub.neighbourhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponse {

    private List<CartItemResponse> items;

    private Integer totalItems;

    private Double totalAmount;
}