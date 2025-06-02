/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.service;

/**
 *
 * @author ADMIN
 */
import com.doancuoimon.realtimechat.dto.request.UserCreationRequest;
import com.doancuoimon.realtimechat.entity.User;
import com.doancuoimon.realtimechat.entity.UserDetailsImpl;
import com.doancuoimon.realtimechat.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(@RequestBody UserCreationRequest request) {
        if (!Objects.isNull(request)) {
            Optional<User> u = userRepository.findByUsername(request.getUsername());
            if(u.isEmpty())
            {
                User user = new User();
                var userID = "U" + System.currentTimeMillis();
                user.setUserid(userID);

                user.setUsername(request.getUsername());

                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setNickname(request.getNickname());
                user.setStatus(1);
                return userRepository.save(user);
            }
            else
                throw new IllegalArgumentException("Username này đã tồn tại");
        }
        return null;
    }// Tạo mới một user

    public User connected(String id) {
        User user = getUser(id);

        user.setStatus(1);
        return userRepository.save(user);
    }

    public User disconnected(String id) {
        User user = getUser(id);

        user.setStatus(2);
        return userRepository.save(user);
    }

    public User getUser(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    } // Tìm một User theo id

    public List<User> getConnectedUsers() {
        return userRepository.findAllByStatus(1);
    }// Lấy ra các user có trạng thái là đang hoạt động

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Dang nhap username {}", username);
        Optional<User> optUser = userRepository.findByUsername(username); // Tìm user trong db bằng JPA sau đó trả về object Optional<User> để kiểm tra null

        if (optUser.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Không thể tìm thấy username %s", username));
        } else
            return new UserDetailsImpl(optUser.get()); // Trả về object thực thi interface UserDetails để xử lý xác thực, ủy quyền
    }
}
