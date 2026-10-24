package com.example.jwt_auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/profile")
    public Map<String, String> profile(
            Authentication authentication) {

        return Map.of(
                "message", "You accessed a protected endpoint!",
                "username", authentication.getName()
        );
    }
}
