/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.service;

import com.doancuoimon.realtimechat.dto.request.ChatroomCreationRequest;
import com.doancuoimon.realtimechat.dto.response.ChatroomResponse;

/**
 * @author ADMIN
 */

import com.doancuoimon.realtimechat.entity.Chatroom;
import com.doancuoimon.realtimechat.entity.Message;
import com.doancuoimon.realtimechat.entity.User;
import com.doancuoimon.realtimechat.repository.ChatroomRepository;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatRoomService {
    @Autowired
    private ChatroomRepository chatroomRepository;
    @Autowired
    private UserService userService;

    public Chatroom createChatroom(ChatroomCreationRequest request) {
        Chatroom chatroom = new Chatroom();

        List<User> listNguoiNhans = userService.getUserByUserids(request.getUseridNguoiNhans());
        String chatID = "chat_" + System.currentTimeMillis();
        chatroom.setIdChatroom(chatID);
        chatroom.setIdChude(request.getIdChuDe());
        chatroom.getChatroomMembers().addAll(listNguoiNhans);

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

    public List<ChatroomResponse> returnAvailableChatResponseForUser(User user) {
        // 1. Get all chatrooms where the user is a member
        List<Chatroom> chatrooms = chatroomRepository.findAllByChatroomMembersUserid(user.getUserid()).stream()
                .filter(c -> c.getChatroomMembers().size() > 1)
                .collect(Collectors.toList());

        // 2. Map chatroom to response, sort by latest message (thoigiangui)
        List<ChatroomResponse> responses = chatrooms.stream()
                .map(chatroom -> {
                    Message latestMsg = chatroom.getMessages().stream()
                            .max(Comparator.comparing(Message::getThoigiangui,
                                    Comparator.nullsFirst(Comparator.naturalOrder())))
                            .orElse(null);
                    return ChatroomResponse.toDto(chatroom, latestMsg);
                })
                .sorted((a, b) -> {
                    Date dateA = a.latestMessage() != null ? a.latestMessage().getThoigiangui() : null;
                    Date dateB = b.latestMessage() != null ? b.latestMessage().getThoigiangui() : null;
                    if (dateA == null && dateB == null)
                        return 0;
                    if (dateA == null)
                        return 1;
                    if (dateB == null)
                        return -1;
                    return dateB.compareTo(dateA);
                })
                .collect(Collectors.toList());

        // 3. Find all users except current
        List<User> allUsers = userService.getConnectedUsers().stream()
                .filter(u -> !u.getUsername().equals(user.getUsername()))
                .toList();

        // 4. For each user, if no private chatroom exists, create a virtual
        // ChatroomResponse
        for (User other : allUsers) {
            boolean exists = chatrooms.stream()
                    .anyMatch(c -> c.getChatroomMembers().stream()
                            .anyMatch(mem -> mem.getUsername().equals(other.getUsername())));
            if (!exists) {
                Chatroom virtualRoom = new Chatroom();
                virtualRoom.setTenchatroom(other.getUsername());
                virtualRoom.setChatroomMembers(Arrays.asList(user, other));
                responses.add(ChatroomResponse.toDto(virtualRoom, null));
            }
        }

        // 5. Resort after adding virtual rooms
        responses.sort((a, b) -> {
            Date dateA = a.latestMessage() != null ? a.latestMessage().getThoigiangui() : null;
            Date dateB = b.latestMessage() != null ? b.latestMessage().getThoigiangui() : null;
            if (dateA == null && dateB == null)
                return 0;
            if (dateA == null)
                return 1;
            if (dateB == null)
                return -1;
            return dateB.compareTo(dateA);
        });

        return responses;
    }
}
