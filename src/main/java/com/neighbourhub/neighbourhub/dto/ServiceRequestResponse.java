package com.neighbourhub.neighbourhub.dto;

import com.neighbourhub.neighbourhub.entity.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestResponse {

    private Long id;

    private Long serviceId;
    private String serviceName;

    private Long customerId;
    private String customerName;
    private String customerPhone;

    private String message;
    private String address;

    private RequestStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}