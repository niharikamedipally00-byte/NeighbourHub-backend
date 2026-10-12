package com.neighbourhub.neighbourhub.service;

import com.neighbourhub.neighbourhub.dto.CreateProductRequest;
import com.neighbourhub.neighbourhub.dto.ProductResponse;
import com.neighbourhub.neighbourhub.entity.Product;
import com.neighbourhub.neighbourhub.entity.Role;
import com.neighbourhub.neighbourhub.entity.Shop;
import com.neighbourhub.neighbourhub.entity.User;
import com.neighbourhub.neighbourhub.repository.ProductRepository;
import com.neighbourhub.neighbourhub.repository.ShopRepository;
import com.neighbourhub.neighbourhub.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public ProductService(
            ProductRepository productRepository,
            ShopRepository shopRepository,
            UserRepository userRepository
    ) {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found"
                        )
                );

        return toResponse(product);
    }

    public List<ProductResponse> getProductsByShop(Long shopId) {
        return productRepository.findByShopId(shopId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> getAvailableProducts() {
        return productRepository.findByAvailableTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse createProduct(
            CreateProductRequest request,
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Shop not found"
                        )
                );

        boolean isAdmin =
                user.getRole() == Role.ADMIN;

        boolean isOwner =
                shop.getOwner().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only add products to your own shop"
            );
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .available(
                        request.getAvailable() != null
                                ? request.getAvailable()
                                : true
                )
                .stockQuantity(
                        request.getStockQuantity() != null
                                ? request.getStockQuantity()
                                : 0
                )
                .shop(shop)
                .createdAt(LocalDateTime.now())
                .build();

        Product savedProduct =
                productRepository.save(product);

        return toResponse(savedProduct);
    }

    public ProductResponse updateProduct(
            Long id,
            CreateProductRequest request,
            String email
    ) {

        Product existingProduct =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Product not found"
                                )
                        );

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found"
                                )
                        );

        Shop shop =
                existingProduct.getShop();

        boolean isAdmin =
                user.getRole() == Role.ADMIN;

        boolean isOwner =
                shop.getOwner().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only update your own products"
            );
        }

        existingProduct.setName(request.getName());
        existingProduct.setDescription(
                request.getDescription()
        );
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCategory(
                request.getCategory()
        );
        existingProduct.setImageUrl(
                request.getImageUrl()
        );

        existingProduct.setAvailable(
                request.getAvailable() != null
                        ? request.getAvailable()
                        : true
        );

        existingProduct.setStockQuantity(
                request.getStockQuantity() != null
                        ? request.getStockQuantity()
                        : 0
        );

        Product savedProduct =
                productRepository.save(existingProduct);

        return toResponse(savedProduct);
    }

    public void deleteProduct(
            Long id,
            String email
    ) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Product not found"
                                )
                        );

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found"
                                )
                        );

        Shop shop =
                product.getShop();

        boolean isAdmin =
                user.getRole() == Role.ADMIN;

        boolean isOwner =
                shop.getOwner().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only delete your own products"
            );
        }

        productRepository.delete(product);
    }

    private ProductResponse toResponse(Product product) {

        Shop shop = product.getShop();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                product.getAvailable(),
                product.getStockQuantity(),
                shop.getId(),
                shop.getName(),
                product.getCreatedAt()
        );
    }
}