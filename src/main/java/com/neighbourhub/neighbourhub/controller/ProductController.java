package com.neighbourhub.neighbourhub.controller;

import com.neighbourhub.neighbourhub.dto.CreateProductRequest;
import com.neighbourhub.neighbourhub.dto.ProductResponse;
import com.neighbourhub.neighbourhub.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;

    public ProductController(
            ProductService productService) {

        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>>
    getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse>
    getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ProductResponse>>
    getProductsByShop(
            @PathVariable Long shopId) {

        return ResponseEntity.ok(
                productService.getProductsByShop(shopId)
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>>
    getProductsByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(
                        category
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>>
    searchProducts(
            @RequestParam String name) {

        return ResponseEntity.ok(
                productService.searchProducts(name)
        );
    }

    @GetMapping("/available")
    public ResponseEntity<List<ProductResponse>>
    getAvailableProducts() {

        return ResponseEntity.ok(
                productService.getAvailableProducts()
        );
    }

    @PostMapping
    public ResponseEntity<ProductResponse>
    createProduct(
            @RequestBody CreateProductRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                productService.createProduct(
                        request,
                        email
                )
        );
    }
    @PutMapping("/{id}")
public ResponseEntity<ProductResponse> updateProduct(
        @PathVariable Long id,
        @RequestBody CreateProductRequest request,
        Authentication authentication
) {
    String email = authentication.getName();

    return ResponseEntity.ok(
            productService.updateProduct(
                    id,
                    request,
                    email
            )
    );
}

@DeleteMapping("/{id}")
public ResponseEntity<String> deleteProduct(
        @PathVariable Long id,
        Authentication authentication
) {
    String email = authentication.getName();

    productService.deleteProduct(
            id,
            email
    );

    return ResponseEntity.ok(
            "Product deleted successfully"
    );
}
}