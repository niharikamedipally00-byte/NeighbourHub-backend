package com.neighbourhub.neighbourhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NearbyShop {

    private String name;
    private String category;
    private String rating;
    private String distance;
    private String status;
    private String description;
}