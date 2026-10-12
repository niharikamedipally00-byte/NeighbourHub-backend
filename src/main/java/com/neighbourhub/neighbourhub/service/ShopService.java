package com.neighbourhub.neighbourhub.service;

import com.neighbourhub.neighbourhub.dto.CreateShopRequest;
import com.neighbourhub.neighbourhub.dto.ShopResponse;
import com.neighbourhub.neighbourhub.entity.Role;
import com.neighbourhub.neighbourhub.entity.Shop;
import com.neighbourhub.neighbourhub.entity.User;
import com.neighbourhub.neighbourhub.repository.ShopRepository;
import com.neighbourhub.neighbourhub.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public ShopService(
            ShopRepository shopRepository,
            UserRepository userRepository) {

        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    public List<ShopResponse> getAllShops() {
        return shopRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ShopResponse getShopById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Shop not found"
                        ));

        return toResponse(shop);
    }

    public List<ShopResponse> getShopsByCategory(String category) {
        return shopRepository
                .findByCategoryIgnoreCase(category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ShopResponse> getShopsByCity(String city) {
        return shopRepository
                .findByCityIgnoreCase(city)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ShopResponse> searchShops(String name) {
        return shopRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ShopResponse createShop(
            CreateShopRequest request,
            String email) {

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        ));

        if (owner.getRole() != Role.SHOP_OWNER &&
                owner.getRole() != Role.ADMIN) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only shop owners can create shops"
            );
        }

        Shop shop = Shop.builder()
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .rating(0.0)
                .open(request.getOpen() != null
                        ? request.getOpen()
                        : true)
                .createdAt(LocalDateTime.now())
                .owner(owner)
                .build();

        Shop savedShop = shopRepository.save(shop);

        return toResponse(savedShop);
    }

    public ShopResponse updateShop(
        Long id,
        Shop updatedShop,
        String email) {

    Shop existingShop = shopRepository.findById(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Shop not found"
                    ));

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found"
                    ));

    boolean isAdmin = user.getRole() == Role.ADMIN;

    boolean isOwner =
            existingShop.getOwner().getId().equals(user.getId());

    if (!isAdmin && !isOwner) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You can only update your own shop"
        );
    }

    existingShop.setName(updatedShop.getName());
    existingShop.setCategory(updatedShop.getCategory());
    existingShop.setDescription(updatedShop.getDescription());
    existingShop.setPhone(updatedShop.getPhone());
    existingShop.setAddress(updatedShop.getAddress());
    existingShop.setCity(updatedShop.getCity());
    existingShop.setLatitude(updatedShop.getLatitude());
    existingShop.setLongitude(updatedShop.getLongitude());
    existingShop.setRating(updatedShop.getRating());
    existingShop.setOpen(updatedShop.getOpen());

    Shop savedShop = shopRepository.save(existingShop);

    return toResponse(savedShop);
}

    public void deleteShop(
        Long id,
        String email) {

    Shop shop = shopRepository.findById(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Shop not found"
                    ));

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found"
                    ));

    boolean isAdmin = user.getRole() == Role.ADMIN;

    boolean isOwner =
            shop.getOwner().getId().equals(user.getId());

    if (!isAdmin && !isOwner) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You can only delete your own shop"
        );
    }

    shopRepository.delete(shop);
}

    private ShopResponse toResponse(Shop shop) {

        User owner = shop.getOwner();

        return new ShopResponse(
                shop.getId(),
                shop.getName(),
                shop.getCategory(),
                shop.getDescription(),
                shop.getPhone(),
                shop.getAddress(),
                shop.getCity(),
                shop.getLatitude(),
                shop.getLongitude(),
                shop.getRating(),
                shop.getOpen(),
                shop.getCreatedAt(),
                owner.getId(),
                owner.getName(),
                owner.getEmail()
        );
    }
}