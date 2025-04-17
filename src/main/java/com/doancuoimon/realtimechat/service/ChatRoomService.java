/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.service;

/**
 *
 * @author ADMIN
 */
import com.doancuoimon.realtimechat.dto.request.ChatroomCreationRequest;
import com.doancuoimon.realtimechat.entity.Chatroom;
import com.doancuoimon.realtimechat.repository.ChatroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomService {
    @Autowired
    private ChatroomRepository chatroomRepository;

    public Chatroom createChatRoom(ChatroomCreationRequest chatRoomCreationRequest) {
        Chatroom chatroom = new Chatroom();

        var chatID = "chat_" + System.currentTimeMillis();
        chatroom.setIdChatroom(chatID);
        chatroom.setTenchatroom(chatRoomCreationRequest.getTenchatroom());
        chatroom.setIdChude(chatRoomCreationRequest.getIdChuDe());
        chatroom.setChatroomMembers(chatRoomCreationRequest.getChatroomMembers());
        chatroomRepository.save(chatroom);
        return chatroom;
    }

    public Chatroom getChatroom(String chatId) {
        return chatroomRepository.findById(chatId).orElse(null);
    }
}
