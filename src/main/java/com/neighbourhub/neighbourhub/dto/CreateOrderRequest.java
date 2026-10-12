package com.neighbourhub.neighbourhub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderRequest {

    @NotBlank
    private String deliveryAddress;

    private String phone;
}