package com.neighbourhub.neighbourhub.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponse {

    private Long id;

    private Long productId;
    private String productName;
    private String description;
    private Double price;
    private String category;
    private String imageUrl;
    private Boolean available;
    private Integer stockQuantity;

    private Long shopId;
    private String shopName;

    private String addedAt;
}