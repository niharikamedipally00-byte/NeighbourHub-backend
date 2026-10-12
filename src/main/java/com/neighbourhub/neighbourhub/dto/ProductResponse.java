package com.neighbourhub.neighbourhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private String imageUrl;
    private Boolean available;
    private Integer stockQuantity;

    private Long shopId;
    private String shopName;

    private LocalDateTime createdAt;
}