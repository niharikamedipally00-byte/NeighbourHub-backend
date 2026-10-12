package com.neighbourhub.neighbourhub.controller;

import com.neighbourhub.neighbourhub.dto.CreateServiceRequest;
import com.neighbourhub.neighbourhub.dto.ServiceRequestResponse;
import com.neighbourhub.neighbourhub.entity.RequestStatus;
import com.neighbourhub.neighbourhub.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    @PostMapping
    public ResponseEntity<ServiceRequestResponse> createRequest(
            @RequestBody CreateServiceRequest request
    ) {
        return ResponseEntity.ok(
                serviceRequestService.createRequest(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequestResponse> getRequestById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                serviceRequestService.getRequestById(id)
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ServiceRequestResponse>> getCustomerRequests(
            @PathVariable Long customerId
    ) {
        return ResponseEntity.ok(
                serviceRequestService.getCustomerRequests(customerId)
        );
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ServiceRequestResponse>> getProviderRequests(
            @PathVariable Long providerId
    ) {
        return ResponseEntity.ok(
                serviceRequestService.getProviderRequests(providerId)
        );
    }

    @GetMapping("/provider/{providerId}/status/{status}")
    public ResponseEntity<List<ServiceRequestResponse>> getProviderRequestsByStatus(
            @PathVariable Long providerId,
            @PathVariable RequestStatus status
    ) {
        return ResponseEntity.ok(
                serviceRequestService.getProviderRequestsByStatus(
                        providerId,
                        status
                )
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ServiceRequestResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam RequestStatus status
    ) {
        return ResponseEntity.ok(
                serviceRequestService.updateStatus(
                        id,
                        status
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRequest(
            @PathVariable Long id
    ) {
        serviceRequestService.deleteRequest(id);

        return ResponseEntity.ok(
                "Service request deleted successfully"
        );
    }
}