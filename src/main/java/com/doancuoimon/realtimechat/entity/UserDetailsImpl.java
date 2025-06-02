package com.doancuoimon.realtimechat.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Triển khai UserDetails để xác thực và phân quyền người dùng trong Spring Security.
 * <br/>
 * Lớp này đại diện cho thông tin người dùng đã đăng nhập, dùng cho quá trình
 * xác thực (authentication)
 * và phân quyền (authorization) trong hệ thống.
 */
public record UserDetailsImpl(User user) implements UserDetails {
    @Override
    public String getPassword() {
        return user().getPassword().trim();
    }

    @Override
    public String getUsername() {
        return user().getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
