package com.neighbourhub.neighbourhub.service;

import com.neighbourhub.neighbourhub.dto.*;
import com.neighbourhub.neighbourhub.entity.Role;
import com.neighbourhub.neighbourhub.entity.User;
import com.neighbourhub.neighbourhub.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;

    public DashboardResponse getDashboard(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );

        return switch (user.getRole()) {

            case CUSTOMER ->
                    customerDashboard(userInfo);

            case SHOP_OWNER ->
                    shopOwnerDashboard(userInfo);

            case SERVICE_PROVIDER ->
                    serviceProviderDashboard(userInfo);

            case DELIVERY_PARTNER ->
                    deliveryDashboard(userInfo);

            case ADMIN ->
                    adminDashboard(userInfo);
        };
    }

    private DashboardResponse customerDashboard(UserInfo user) {

        List<QuickAction> actions = List.of(
                new QuickAction("Nearby Shops", "shop"),
                new QuickAction("Services", "service"),
                new QuickAction("Track Order", "track"),
                new QuickAction("Saved", "saved")
        );

        List<NearbyShop> shops = List.of(

                new NearbyShop(
                        "Sri Krishna Sweets",
                        "Sweets & Bakery",
                        "4.8",
                        "0.4 km",
                        "Open Now",
                        "Fresh sweets & snacks"
                ),

                new NearbyShop(
                        "Nature's Basket",
                        "Grocery",
                        "4.7",
                        "0.8 km",
                        "Open Now",
                        "Fresh groceries nearby"
                )
        );

        List<ServiceProvider> services = List.of(

                new ServiceProvider(
                        "Ramesh Electricals & AC Care",
                        "AC servicing, inverters & electrical work",
                        "4.9",
                        "From ₹299",
                        true
                ),

                new ServiceProvider(
                        "UrbanClean Deep Sanitizing",
                        "Full kitchen & bathroom deep cleaning",
                        "4.8",
                        "From ₹799",
                        true
                )
        );

        List<LiveOrder> orders = List.of(
                new LiveOrder(
                        "#NHI-8492",
                        "Local Grocery Order",
                        "Out for Delivery",
                        "₹480",
                        "12 mins"
                )
        );

        List<CommunityNotice> notices = List.of(
                new CommunityNotice(
                        "Weekend Organ Farmers Market",
                        "Defence Colony Children's Park",
                        "Today • 10:00 AM - 1:00 PM"
                )
        );

        return new DashboardResponse(
                user,
                Role.CUSTOMER,
                actions,
                shops,
                services,
                orders,
                notices
        );
    }

    private DashboardResponse shopOwnerDashboard(UserInfo user) {

        List<QuickAction> actions = List.of(
                new QuickAction("My Shop", "shop"),
                new QuickAction("Add Product", "add"),
                new QuickAction("Orders", "orders"),
                new QuickAction("Analytics", "analytics")
        );

        return new DashboardResponse(
                user,
                Role.SHOP_OWNER,
                actions,
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    private DashboardResponse serviceProviderDashboard(UserInfo user) {

        List<QuickAction> actions = List.of(
                new QuickAction("My Services", "service"),
                new QuickAction("Add Service", "add"),
                new QuickAction("Requests", "requests"),
                new QuickAction("Availability", "calendar")
        );

        return new DashboardResponse(
                user,
                Role.SERVICE_PROVIDER,
                actions,
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    private DashboardResponse deliveryDashboard(UserInfo user) {

        List<QuickAction> actions = List.of(
                new QuickAction("Available", "delivery"),
                new QuickAction("Active Delivery", "track"),
                new QuickAction("History", "history"),
                new QuickAction("Earnings", "money")
        );

        return new DashboardResponse(
                user,
                Role.DELIVERY_PARTNER,
                actions,
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    private DashboardResponse adminDashboard(UserInfo user) {

        return new DashboardResponse(
                user,
                Role.ADMIN,
                List.of(
                        new QuickAction("Users", "users"),
                        new QuickAction("Shops", "shop"),
                        new QuickAction("Services", "service"),
                        new QuickAction("Reports", "reports")
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }
}