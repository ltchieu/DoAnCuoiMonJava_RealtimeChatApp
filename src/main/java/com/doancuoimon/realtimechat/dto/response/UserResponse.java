package com.doancuoimon.realtimechat.dto.response;

import com.doancuoimon.realtimechat.entity.User;

public record UserResponse(
        String userid,
        String username,
        String nickname) {
    public static UserResponse toDto(User user) {
        return new UserResponse(user.getUserid().trim(), user.getUsername(), user.getNickname()); 
    } 
}
