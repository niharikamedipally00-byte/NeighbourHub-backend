package com.neighbourhub.neighbourhub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShopRequest {

    private String name;

    private String category;

    private String description;

    private String phone;

    private String address;

    private String city;

    private Double latitude;

    private Double longitude;

    private Boolean open;
}