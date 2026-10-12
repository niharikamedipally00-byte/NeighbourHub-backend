package com.neighbourhub.neighbourhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ShopResponse {

    private Long id;

    private String name;

    private String category;

    private String description;

    private String phone;

    private String address;

    private String city;

    private Double latitude;

    private Double longitude;

    private Double rating;

    private Boolean open;

    private LocalDateTime createdAt;

    private Long ownerId;

    private String ownerName;

    private String ownerEmail;
}