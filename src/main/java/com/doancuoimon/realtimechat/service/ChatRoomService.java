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
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class ChatRoomService {
    @Autowired
    private ChatroomRepository chatroomRepository;
    @Autowired
    private ChatroomMemberRepository chatroomMemberRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EntityManager entityManager;


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Chatroom createChatRoom(ChatroomCreationRequest chatRoomCreationRequest) {

        if(chatRoomCreationRequest.getNguoiNhans() == null){
            throw new RuntimeException("Khong nhan duoc danh sach nguoi nhan tu client");
        }
        Chatroom chatroom = new Chatroom();
        User user = new User();
        List<User> nguoiNhans = chatRoomCreationRequest.getNguoiNhans();
        List<ChatroomMember> members = new ArrayList<>();

        var chatID = "chat_" + System.currentTimeMillis();
        //Tạo mới một phòng chat
        chatroom.setIdChatroom(chatID);
        chatroom.setIdChude(chatRoomCreationRequest.getIdChuDe());
        chatroom.setNgaylap(LocalDate.now());
        if(nguoiNhans.size() == 1)
        {
            String nickname = user.getNickname();
            chatroom.setTenchatroom(nickname);
        }
        else {
            chatroom.setTenchatroom(chatRoomCreationRequest.getTenchatroom());
        }

        chatroomRepository.save(chatroom);
        for(User member : nguoiNhans){
            user = entityManager.getReference(User.class, member.getUserid());
            ChatroomMemberId chatroomMemberId = new ChatroomMemberId();
            chatroomMemberId.setIdChatroom(chatID);
            chatroomMemberId.setIdNguoinhan(user.getUserid());

            ChatroomMember chatroomMember = new ChatroomMember(chatroom, user, Instant.now(), chatroomMemberId);
            members.add(chatroomMember);
        }

        chatroom.setChatroomMembers(members);
        chatroomRepository.save(chatroom);
        return chatroom;
    }

    public Chatroom getChatroom(String chatId) {
        return chatroomRepository.findById(chatId.trim()).orElse(null);
    }

    public List<User> getChatroomMembers(String chatId) {
        Chatroom c = getChatroom(chatId);
        if(c == null){
            return null;
        }
        else{
            List<User> lstUsers = new ArrayList<>();
            for(ChatroomMember member : c.getChatroomMembers()){
                lstUsers.add(member.getIdNguoinhan());
            }
            return lstUsers;
        }
    }
}
