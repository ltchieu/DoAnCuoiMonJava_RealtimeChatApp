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
import com.doancuoimon.realtimechat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(UserCreationRequest request){
        User user = new User();
        user.setUserid(request.getUserid());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setStatus(1);
        return userRepository.save(user);
    }//Tạo mới một user

    public User connected(String username){
        User user = getIdUser(username);

        user.setStatus(1);
        return userRepository.save(user);
    }

    public User disconnected(String username){
        User user = getIdUser(username);

        user.setStatus(2);
        return userRepository.save(user);
    }

    public User getIdUser(String username){
        return userRepository.findById(username).orElseThrow( () -> new RuntimeException("User not found"));
    } //Tìm id của một User

    public List<User> getConnectedUsers(){
        return userRepository.findAllByStatus(1);
    }//Lấy ra các user có trạng thái là đang hoạt động
}

