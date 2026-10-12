package com.neighbourhub.neighbourhub.controller;

import com.neighbourhub.neighbourhub.dto.CreateShopRequest;
import com.neighbourhub.neighbourhub.dto.ShopResponse;
import com.neighbourhub.neighbourhub.entity.Shop;
import com.neighbourhub.neighbourhub.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = "http://localhost:5173")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    public ResponseEntity<List<ShopResponse>> getAllShops() {
        return ResponseEntity.ok(
                shopService.getAllShops()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopResponse> getShopById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                shopService.getShopById(id)
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ShopResponse>> getShopsByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                shopService.getShopsByCategory(category)
        );
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<ShopResponse>> getShopsByCity(
            @PathVariable String city) {

        return ResponseEntity.ok(
                shopService.getShopsByCity(city)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ShopResponse>> searchShops(
            @RequestParam String name) {

        return ResponseEntity.ok(
                shopService.searchShops(name)
        );
    }

    @PostMapping
    public ResponseEntity<ShopResponse> createShop(
            @RequestBody CreateShopRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                shopService.createShop(request, email)
        );
    }

    @PutMapping("/{id}")
public ResponseEntity<ShopResponse> updateShop(
        @PathVariable Long id,
        @RequestBody Shop shop,
        Authentication authentication) {

    String email = authentication.getName();

    return ResponseEntity.ok(
            shopService.updateShop(
                    id,
                    shop,
                    email
            )
    );
}
}