package com.neighbourhub.neighbourhub.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {

    private Long id;
    private String name;
    private String description;
    private String category;
    private Double price;
    private String phone;
    private String location;
    private Boolean available;

    private Long providerId;
    private String providerName;
    private String providerEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}