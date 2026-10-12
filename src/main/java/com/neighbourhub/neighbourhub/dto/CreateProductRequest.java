package com.neighbourhub.neighbourhub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequest {

    private String name;

    private String description;

    private Double price;

    private String category;

    private String imageUrl;

    private Boolean available;

    private Integer stockQuantity;

    private Long shopId;
}