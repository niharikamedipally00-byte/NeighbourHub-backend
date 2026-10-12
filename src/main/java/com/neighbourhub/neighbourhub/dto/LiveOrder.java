package com.neighbourhub.neighbourhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LiveOrder {

    private String orderId;
    private String title;
    private String status;
    private String amount;
    private String estimatedTime;
}