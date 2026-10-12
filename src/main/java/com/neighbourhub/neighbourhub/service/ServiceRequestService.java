package com.neighbourhub.neighbourhub.service;

import com.neighbourhub.neighbourhub.dto.CreateServiceRequest;
import com.neighbourhub.neighbourhub.dto.ServiceRequestResponse;
import com.neighbourhub.neighbourhub.entity.RequestStatus;
import com.neighbourhub.neighbourhub.entity.Service;
import com.neighbourhub.neighbourhub.entity.ServiceRequest;
import com.neighbourhub.neighbourhub.entity.User;
import com.neighbourhub.neighbourhub.repository.ServiceRepository;
import com.neighbourhub.neighbourhub.repository.ServiceRequestRepository;
import com.neighbourhub.neighbourhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceRequestService {

    private final ServiceRequestRepository requestRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public ServiceRequestResponse createRequest(
            CreateServiceRequest request
    ) {
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ServiceRequest serviceRequest = ServiceRequest.builder()
                .service(service)
                .customer(customer)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .message(request.getMessage())
                .address(request.getAddress())
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toResponse(
                requestRepository.save(serviceRequest)
        );
    }

    public ServiceRequestResponse getRequestById(Long id) {
        return requestRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Service request not found"
                        )
                );
    }

    public List<ServiceRequestResponse> getCustomerRequests(
            Long customerId
    ) {
        return requestRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ServiceRequestResponse> getProviderRequests(
            Long providerId
    ) {
        return requestRepository
                .findByService_Provider_Id(providerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ServiceRequestResponse> getProviderRequestsByStatus(
            Long providerId,
            RequestStatus status
    ) {
        return requestRepository
                .findByService_Provider_IdAndStatus(
                        providerId,
                        status
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ServiceRequestResponse updateStatus(
            Long id,
            RequestStatus status
    ) {
        ServiceRequest request = requestRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Service request not found"
                        )
                );

        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());

        return toResponse(
                requestRepository.save(request)
        );
    }

    public void deleteRequest(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new RuntimeException(
                    "Service request not found"
            );
        }

        requestRepository.deleteById(id);
    }

    private ServiceRequestResponse toResponse(
            ServiceRequest request
    ) {
        Service service = request.getService();
        User customer = request.getCustomer();

        return ServiceRequestResponse.builder()
                .id(request.getId())
                .serviceId(
                        service != null
                                ? service.getId()
                                : null
                )
                .serviceName(
                        service != null
                                ? service.getName()
                                : null
                )
                .customerId(
                        customer != null
                                ? customer.getId()
                                : null
                )
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .message(request.getMessage())
                .address(request.getAddress())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}