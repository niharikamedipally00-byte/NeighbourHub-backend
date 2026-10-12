package com.neighbourhub.neighbourhub.service;

import com.neighbourhub.neighbourhub.dto.CreateOrderRequest;
import com.neighbourhub.neighbourhub.dto.OrderResponse;
import com.neighbourhub.neighbourhub.entity.CartItem;
import com.neighbourhub.neighbourhub.entity.Order;
import com.neighbourhub.neighbourhub.entity.OrderItem;
import com.neighbourhub.neighbourhub.entity.OrderStatus;
import com.neighbourhub.neighbourhub.entity.Product;
import com.neighbourhub.neighbourhub.entity.Role;
import com.neighbourhub.neighbourhub.entity.Shop;
import com.neighbourhub.neighbourhub.entity.User;
import com.neighbourhub.neighbourhub.repository.CartItemRepository;
import com.neighbourhub.neighbourhub.repository.OrderItemRepository;
import com.neighbourhub.neighbourhub.repository.OrderRepository;
import com.neighbourhub.neighbourhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final String EMAIL = "customer@example.com";
    private static final Long USER_ID = 15L;
    private static final Long ORDER_ID = 82L;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Shop shop;
    private Product product;
    private CreateOrderRequest request;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        user = User.builder()
                .id(USER_ID)
                .name("Riya Sharma")
                .email(EMAIL)
                .password("encoded-password")
                .phone("9876543210")
                .role(Role.CUSTOMER)
                .createdAt(now)
                .build();
        shop = Shop.builder()
                .id(25L)
                .name("Green Basket")
                .category("Groceries")
                .description("Fresh local groceries")
                .phone("9876501234")
                .address("12 Market Road")
                .city("Pune")
                .rating(4.7)
                .open(true)
                .createdAt(now)
                .owner(user)
                .build();
        product = Product.builder()
                .id(41L)
                .name("Organic Apples")
                .description("Locally grown apples")
                .price(12.50)
                .category("Fruit")
                .available(true)
                .stockQuantity(10)
                .shop(shop)
                .createdAt(now)
                .build();
        request = new CreateOrderRequest();
        request.setDeliveryAddress("  24 Lake View, Pune  ");
        request.setPhone("9123456780");

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));
    }

    @Test
    void createOrderCreatesOrderFromCartItems() {
        CartItem cartItem = cartItem(product, 2);
        when(cartItemRepository.findByUser(user))
                .thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(ORDER_ID);
                    return order;
                });

        OrderResponse response = orderService.createOrder(request, EMAIL);

        assertEquals(ORDER_ID, response.getId());
        assertEquals(OrderStatus.PLACED, response.getStatus());
        assertEquals("24 Lake View, Pune", response.getDeliveryAddress());
        assertEquals("9123456780", response.getPhone());
        assertEquals(25.0, response.getTotalAmount());
        assertEquals(1, response.getItems().size());
        assertEquals(product.getId(), response.getItems().get(0).getProductId());
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(cartItemRepository).deleteByUser(user);
    }

    @Test
    void createOrderFailsWhenCartIsEmpty() {
        when(cartItemRepository.findByUser(user)).thenReturn(List.of());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(request, EMAIL)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("cart is empty"));
        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void createOrderFailsWhenProductIsUnavailable() {
        product.setAvailable(false);
        when(cartItemRepository.findByUser(user))
                .thenReturn(List.of(cartItem(product, 1)));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(request, EMAIL)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("currently unavailable"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderFailsWhenStockIsInsufficient() {
        product.setStockQuantity(1);
        when(cartItemRepository.findByUser(user))
                .thenReturn(List.of(cartItem(product, 2)));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.createOrder(request, EMAIL)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("Insufficient stock"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderCalculatesCorrectTotalAmount() {
        Product secondProduct = product(42L, "Sourdough Bread", 7.0, 8, shop);
        when(cartItemRepository.findByUser(user))
                .thenReturn(List.of(
                        cartItem(product, 2),
                        cartItem(secondProduct, 3)
                ));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.createOrder(request, EMAIL);

        assertEquals(46.0, response.getTotalAmount());
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(46.0, orderCaptor.getValue().getTotalAmount());
    }

    @Test
    void createOrderCreatesOrderItemRecordsFromCartItems() {
        when(cartItemRepository.findByUser(user))
                .thenReturn(List.of(cartItem(product, 2)));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(ORDER_ID);
                    return order;
                });

        orderService.createOrder(request, EMAIL);

        ArgumentCaptor<OrderItem> itemCaptor =
                ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository).save(itemCaptor.capture());
        OrderItem savedItem = itemCaptor.getValue();
        assertEquals(ORDER_ID, savedItem.getOrder().getId());
        assertEquals(product, savedItem.getProduct());
        assertEquals("Organic Apples", savedItem.getProductName());
        assertEquals(12.50, savedItem.getPrice());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(25.0, savedItem.getSubtotal());
        assertEquals(shop, savedItem.getShop());
        assertEquals("Green Basket", savedItem.getShopName());
    }

    @Test
    void createOrderDecreasesProductStock() {
        when(cartItemRepository.findByUser(user))
                .thenReturn(List.of(cartItem(product, 3)));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(request, EMAIL);

        assertEquals(7, product.getStockQuantity());
    }

    @Test
    void createOrderMarksProductUnavailableWhenStockReachesZero() {
        product.setStockQuantity(2);
        when(cartItemRepository.findByUser(user))
                .thenReturn(List.of(cartItem(product, 2)));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(request, EMAIL);

        assertEquals(0, product.getStockQuantity());
        assertFalse(product.getAvailable());
    }

    @Test
    void createOrderClearsCartAfterSuccessfulCreation() {
        when(cartItemRepository.findByUser(user))
                .thenReturn(List.of(cartItem(product, 1)));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(request, EMAIL);

        verify(cartItemRepository).deleteByUser(user);
    }

    @Test
    void getMyOrdersReturnsOrdersForCurrentUser() {
        Order order = order(ORDER_ID, OrderStatus.PLACED);
        when(orderRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of(order));
        when(orderItemRepository.findByOrderId(ORDER_ID))
                .thenReturn(List.of());

        List<OrderResponse> responses = orderService.getMyOrders(EMAIL);

        assertEquals(1, responses.size());
        assertEquals(ORDER_ID, responses.get(0).getId());
        assertEquals(OrderStatus.PLACED, responses.get(0).getStatus());
        verify(orderRepository).findByUserOrderByCreatedAtDesc(user);
    }

    @Test
    void getMyOrderReturnsTheUsersSpecificOrder() {
        Order order = order(ORDER_ID, OrderStatus.CONFIRMED);
        when(orderRepository.findByIdAndUserId(ORDER_ID, USER_ID))
                .thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(ORDER_ID))
                .thenReturn(List.of());

        OrderResponse response = orderService.getMyOrder(ORDER_ID, EMAIL);

        assertEquals(ORDER_ID, response.getId());
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());
        verify(orderRepository).findByIdAndUserId(ORDER_ID, USER_ID);
    }

    @Test
    void getMyOrderRejectsOrderThatDoesNotBelongToCurrentUser() {
        when(orderRepository.findByIdAndUserId(ORDER_ID, USER_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.getMyOrder(ORDER_ID, EMAIL)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertEquals("Order not found", exception.getReason());
        verify(orderRepository).findByIdAndUserId(ORDER_ID, USER_ID);
        verifyNoInteractions(orderItemRepository);
    }

    @Test
    void cancelOrderCancelsPlacedOrder() {
        assertOrderCanBeCancelled(OrderStatus.PLACED);
    }

    @Test
    void cancelOrderCancelsConfirmedOrder() {
        assertOrderCanBeCancelled(OrderStatus.CONFIRMED);
    }

    @ParameterizedTest
    @EnumSource(
            value = OrderStatus.class,
            names = {"PREPARING", "READY", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"}
    )
    void cancelOrderRejectsOrdersThatCanNoLongerBeCancelled(OrderStatus status) {
        when(orderRepository.findByIdAndUserId(ORDER_ID, USER_ID))
                .thenReturn(Optional.of(order(ORDER_ID, status)));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.cancelOrder(ORDER_ID, EMAIL)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("can no longer be cancelled"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    private void assertOrderCanBeCancelled(OrderStatus initialStatus) {
        Order order = order(ORDER_ID, initialStatus);
        when(orderRepository.findByIdAndUserId(ORDER_ID, USER_ID))
                .thenReturn(Optional.of(order));
        when(orderRepository.save(order))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.findByOrderId(ORDER_ID))
                .thenReturn(List.of());

        OrderResponse response = orderService.cancelOrder(ORDER_ID, EMAIL);

        assertEquals(OrderStatus.CANCELLED, response.getStatus());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertNotNull(order.getUpdatedAt());
        verify(orderRepository).save(order);
    }

    private CartItem cartItem(Product itemProduct, int quantity) {
        LocalDateTime now = LocalDateTime.now();
        return CartItem.builder()
                .id(1L)
                .user(user)
                .product(itemProduct)
                .quantity(quantity)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private Product product(
            Long id,
            String name,
            double price,
            int stockQuantity,
            Shop productShop
    ) {
        return Product.builder()
                .id(id)
                .name(name)
                .description("Freshly made locally")
                .price(price)
                .category("Bakery")
                .available(true)
                .stockQuantity(stockQuantity)
                .shop(productShop)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Order order(Long id, OrderStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return Order.builder()
                .id(id)
                .user(user)
                .totalAmount(25.0)
                .status(status)
                .deliveryAddress("24 Lake View, Pune")
                .phone("9123456780")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
