package com.example.jwt_auth.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private final String token;
    private final String tokenType = "Bearer";

    public LoginResponse(String token) {
        this.token = token;
    }
}
