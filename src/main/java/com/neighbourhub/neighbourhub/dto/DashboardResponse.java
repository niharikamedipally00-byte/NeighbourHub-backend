package com.neighbourhub.neighbourhub.dto;

import com.neighbourhub.neighbourhub.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardResponse {

    private UserInfo user;
    private Role role;

    private List<QuickAction> quickActions;
    private List<NearbyShop> nearbyShops;
    private List<ServiceProvider> serviceProviders;
    private List<LiveOrder> liveOrders;
    private List<CommunityNotice> communityNotices;
}