package com.neighbourhub.neighbourhub.dto;

import com.neighbourhub.neighbourhub.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
}