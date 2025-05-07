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
import com.doancuoimon.realtimechat.dto.request.MessageCreationRequest;
import com.doancuoimon.realtimechat.entity.Chatroom;
import com.doancuoimon.realtimechat.entity.Message;
import com.doancuoimon.realtimechat.repository.ChatroomRepository;
import com.doancuoimon.realtimechat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRoomService chatRoomService;

    public Message saveMessage(
            MessageCreationRequest messageCreationRequest,
            String chatId
    ) {
        Chatroom chatroom = chatRoomService.getChatroom(chatId);
        Message message = new Message();
        message.setIdMessage(messageCreationRequest.getIdMessage());
        message.setNguoigui(messageCreationRequest.getNguoigui());
        message.setIdChatroom(chatroom);
        message.setNoidungtn(messageCreationRequest.getNoidungtn());
        message.setThoigiangui(messageCreationRequest.getThoigiangui());

        return messageRepository.save(message);
    }

    public List<Message> findChatMessages(String chatId) throws Exception {
        Chatroom chatroom = chatRoomService.getChatroom(chatId);
        if(chatroom == null){
            throw new RuntimeException("Khong tim thay chat room " + chatId);
        }
        return messageRepository.findMessageByidChatroom(chatroom);
    }
}
