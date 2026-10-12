package com.neighbourhub.neighbourhub.controller;

import com.neighbourhub.neighbourhub.dto.ServiceResponse;
import com.neighbourhub.neighbourhub.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getServiceById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ServiceResponse>> getServicesByProvider(
            @PathVariable Long providerId
    ) {
        return ResponseEntity.ok(
                serviceService.getServicesByProvider(providerId)
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ServiceResponse>> getServicesByCategory(
            @PathVariable String category
    ) {
        return ResponseEntity.ok(
                serviceService.getServicesByCategory(category)
        );
    }

    @GetMapping("/available")
    public ResponseEntity<List<ServiceResponse>> getAvailableServices() {
        return ResponseEntity.ok(
                serviceService.getAvailableServices()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServiceResponse>> searchServices(
            @RequestParam String name
    ) {
        return ResponseEntity.ok(
                serviceService.searchServices(name)
        );
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> createService(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "true") Boolean available,
            @RequestParam Long providerId
    ) {
        return ResponseEntity.ok(
                serviceService.createService(
                        name,
                        description,
                        category,
                        price,
                        phone,
                        location,
                        available,
                        providerId
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String location,
            @RequestParam Boolean available
    ) {
        return ResponseEntity.ok(
                serviceService.updateService(
                        id,
                        name,
                        description,
                        category,
                        price,
                        phone,
                        location,
                        available
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(
            @PathVariable Long id
    ) {
        serviceService.deleteService(id);
        return ResponseEntity.ok("Service deleted successfully");
    }
}