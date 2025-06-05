package com.doancuoimon.realtimechat.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.doancuoimon.realtimechat.dto.request.UserCreationRequest;
import com.doancuoimon.realtimechat.dto.response.UserResponse;
import com.doancuoimon.realtimechat.entity.User;
import com.doancuoimon.realtimechat.service.UserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    UserService userService;

    @PostMapping("/sign-up")
    public User signUp(@RequestBody UserCreationRequest request) {
        User registeredUser = userService.createUser(request);
        return registeredUser;
    }

    @GetMapping("/api/session")
    public UserResponse getSession(Authentication authentication) {
        return UserResponse
                .toDto(userService.getUserFromUserDetails(userService.loadUserByUsername(authentication.getName())));
    }
}
