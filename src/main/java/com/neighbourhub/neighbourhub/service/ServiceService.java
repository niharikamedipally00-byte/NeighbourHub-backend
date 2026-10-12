package com.neighbourhub.neighbourhub.service;

import com.neighbourhub.neighbourhub.dto.ServiceResponse;
import com.neighbourhub.neighbourhub.entity.User;
import com.neighbourhub.neighbourhub.repository.ServiceRepository;
import com.neighbourhub.neighbourhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ServiceResponse getServiceById(Long id) {
        return serviceRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    public List<ServiceResponse> getServicesByProvider(Long providerId) {
        return serviceRepository.findByProviderId(providerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ServiceResponse> getServicesByCategory(String category) {
        return serviceRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ServiceResponse> getAvailableServices() {
        return serviceRepository.findByAvailableTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ServiceResponse> searchServices(String name) {
        return serviceRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ServiceResponse createService(
            String name,
            String description,
            String category,
            Double price,
            String phone,
            String location,
            Boolean available,
            Long providerId
    ) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        com.neighbourhub.neighbourhub.entity.Service service =
                com.neighbourhub.neighbourhub.entity.Service.builder()
                        .name(name)
                        .description(description)
                        .category(category)
                        .price(price)
                        .phone(phone)
                        .location(location)
                        .available(available != null ? available : true)
                        .provider(provider)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        return toResponse(serviceRepository.save(service));
    }

    public ServiceResponse updateService(
            Long id,
            String name,
            String description,
            String category,
            Double price,
            String phone,
            String location,
            Boolean available
    ) {
        com.neighbourhub.neighbourhub.entity.Service service =
                serviceRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setName(name);
        service.setDescription(description);
        service.setCategory(category);
        service.setPrice(price);
        service.setPhone(phone);
        service.setLocation(location);
        service.setAvailable(available);
        service.setUpdatedAt(LocalDateTime.now());

        return toResponse(serviceRepository.save(service));
    }

    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new RuntimeException("Service not found");
        }

        serviceRepository.deleteById(id);
    }

    private ServiceResponse toResponse(
            com.neighbourhub.neighbourhub.entity.Service service
    ) {
        User provider = service.getProvider();

        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .category(service.getCategory())
                .price(service.getPrice())
                .phone(service.getPhone())
                .location(service.getLocation())
                .available(service.getAvailable())
                .providerId(provider != null ? provider.getId() : null)
                .providerName(provider != null ? provider.getName() : null)
                .providerEmail(provider != null ? provider.getEmail() : null)
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .build();
    }
}