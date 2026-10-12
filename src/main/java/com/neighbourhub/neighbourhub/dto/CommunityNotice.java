package com.neighbourhub.neighbourhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityNotice {

    private String title;
    private String description;
    private String time;
}