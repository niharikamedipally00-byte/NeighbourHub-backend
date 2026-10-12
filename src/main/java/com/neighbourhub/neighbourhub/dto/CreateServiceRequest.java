package com.neighbourhub.neighbourhub.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceRequest {

    private Long serviceId;
    private Long customerId;

    private String customerName;
    private String customerPhone;

    private String message;
    private String address;
}