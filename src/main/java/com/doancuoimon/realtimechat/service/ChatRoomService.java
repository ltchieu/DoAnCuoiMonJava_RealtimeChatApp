/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.service;

/**
 * @author ADMIN
 */

import com.doancuoimon.realtimechat.dto.request.ChatroomCreationRequest;
import com.doancuoimon.realtimechat.entity.Chatroom;
import com.doancuoimon.realtimechat.entity.ChatroomMember;
import com.doancuoimon.realtimechat.entity.ChatroomMemberId;
import com.doancuoimon.realtimechat.entity.User;
import com.doancuoimon.realtimechat.repository.ChatroomMemberRepository;
import com.doancuoimon.realtimechat.repository.ChatroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChatRoomService {
    @Autowired
    private ChatroomRepository chatroomRepository;
    @Autowired
    private ChatroomMemberRepository chatroomMemberRepository;
    @Autowired
    private UserService userService;

    public Chatroom createChatRoom(ChatroomCreationRequest chatRoomCreationRequest) {
        Chatroom chatroom = new Chatroom();


        var chatID = "chat_" + System.currentTimeMillis();
        //Tạo mới một phòng chat
        chatroom.setIdChatroom(chatID);
        if(chatRoomCreationRequest.getIdNguoiNhan().size() == 1)
        {
            User u = userService.getUser(chatRoomCreationRequest.getIdNguoiNhan().getFirst());
            String nickname = u.getNickname();
            chatroom.setTenchatroom(nickname);
        }
        else {
            chatroom.setTenchatroom(chatRoomCreationRequest.getTenchatroom());
        }
        chatroom.setIdChude(chatRoomCreationRequest.getIdChuDe());
        chatroom.setNgaylap(chatRoomCreationRequest.getNgayTao());
        chatroomRepository.save(chatroom);

        System.out.println("idNguoiNhan: " + chatRoomCreationRequest.getIdNguoiNhan());
        for (String id : chatRoomCreationRequest.getIdNguoiNhan()) {
            ChatroomMemberId chatroomMemberId = new ChatroomMemberId();
            User user = userService.getUser(id);
            //Tạo khóa chính cho ChatroomMemberId
            chatroomMemberId.setIdChatroom(chatID);
            chatroomMemberId.setIdNguoinhan(user.getUserid());
            if (chatroomMemberRepository.existsById(chatroomMemberId)) {
                continue; // Bỏ qua nếu đã tồn tại
            }
            //Tạo mới thành viên của phòng chat
            ChatroomMember chatroomMember = new ChatroomMember();
            chatroomMember.setId(chatroomMemberId);
            chatroomMember.setIdChatroom(chatroom);
            chatroomMember.setIdNguoinhan(user);
            chatroomMember.setNgaythamgia(Instant.now());
            System.out.println("Before saving ChatroomMember: " + chatroomMemberId);
            chatroomMemberRepository.save(chatroomMember);
            System.out.println("After saving ChatroomMember: " + chatroomMemberId);
        }

        return chatroom;
    }

    public Chatroom getChatroom(String chatId) {
        return chatroomRepository.findById(chatId.trim()).orElse(null);
    }
}
