/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.service;

import com.doancuoimon.realtimechat.dto.request.ChatroomCreationRequest;

/**
 * @author ADMIN
 */

import com.doancuoimon.realtimechat.entity.Chatroom;
import com.doancuoimon.realtimechat.entity.User;
import com.doancuoimon.realtimechat.repository.ChatroomRepository;

import io.micrometer.common.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ChatRoomService {
    @Autowired
    private ChatroomRepository chatroomRepository;
    @Autowired
    private UserService userService;

    public Chatroom createChatroom(ChatroomCreationRequest request, User nguoiTao) {
        Chatroom chatroom = new Chatroom();

        List<User> listNguoiNhans = userService.getUserByUsernames(request.getUsernameNguoiNhans());
        if (listNguoiNhans.isEmpty())
            throw new IllegalArgumentException("Khong co nguoi nhan nao");
        String chatID = "chat_" + System.currentTimeMillis();
        chatroom.setIdChatroom(chatID);
        chatroom.setIdChude(request.getIdChuDe());
        chatroom.getChatroomMembers().addAll(listNguoiNhans);
        chatroom.getChatroomMembers().add(nguoiTao);

        if (StringUtils.isEmpty(request.getTenchatroom()))
            chatroom.setTenchatroom(
                    String.join(" & ", chatroom.getChatroomMembers().stream()
                            .map(User::getNickname)
                            .toList()));
        else
            chatroom.setTenchatroom(request.getTenchatroom());
        chatroom.setNgaylap(LocalDate.now());

        return chatroomRepository.save(chatroom);
    }

    public Chatroom getChatroom(String chatId) {
        return chatroomRepository.findById(chatId.trim()).orElse(null);
    }

    public List<User> getChatroomMembers(String chatId) {
        Chatroom c = getChatroom(chatId);
        if (c == null) {
            return null;
        } else {
            List<User> lstUsers = new ArrayList<>();
            for (User member : c.getChatroomMembers()) {
                lstUsers.add(member);
            }
            return lstUsers;
        }
    }

    public Chatroom findPrivateChatroomByUsernames(String senderUsername, String recipientUsername) {
        User sender = userService.getUserFromUserDetails(userService.loadUserByUsername(senderUsername));
        User recipient = userService.getUserFromUserDetails(userService.loadUserByUsername(recipientUsername));
        return chatroomRepository.findChatroomByMembers(sender.getUserid(), recipient.getUserid())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay chatroom"));
    }
}
