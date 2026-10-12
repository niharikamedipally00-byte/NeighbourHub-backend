package com.neighbourhub.neighbourhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neighbourhub.neighbourhub.entity.RequestStatus;
import com.neighbourhub.neighbourhub.entity.ServiceRequest;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByCustomerId(Long customerId);

    List<ServiceRequest> findByService_Provider_Id(Long providerId);

    List<ServiceRequest> findByService_Provider_IdAndStatus(
            Long providerId,
            RequestStatus status
    );

    List<ServiceRequest> findByCustomerIdAndStatus(
            Long customerId,
            RequestStatus status
    );
}