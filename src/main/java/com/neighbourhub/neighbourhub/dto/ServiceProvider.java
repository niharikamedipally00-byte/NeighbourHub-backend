package com.neighbourhub.neighbourhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceProvider {

    private String name;
    private String service;
    private String rating;
    private String price;
    private boolean available;
}