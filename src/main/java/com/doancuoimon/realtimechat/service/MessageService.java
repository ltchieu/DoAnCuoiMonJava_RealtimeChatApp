/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.service;

/**
 *
 * @author ADMIN
 */
import com.doancuoimon.realtimechat.dto.request.AttachmentCreationRequest;
import com.doancuoimon.realtimechat.dto.request.ChatroomCreationRequest;
import com.doancuoimon.realtimechat.dto.request.MessageCreationRequest;
import com.doancuoimon.realtimechat.entity.Attachment;
import com.doancuoimon.realtimechat.entity.Chatroom;
import com.doancuoimon.realtimechat.entity.Message;
import com.doancuoimon.realtimechat.entity.User;
import com.doancuoimon.realtimechat.repository.AttachmentRepository;
import com.doancuoimon.realtimechat.repository.ChatroomRepository;
import com.doancuoimon.realtimechat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private AttachmentRepository attachmentRepository;

    public Message saveMessage(
            MessageCreationRequest messageCreationRequest,
            User nguoiGui
    ) {
        Chatroom chatroom = chatRoomService.getChatroom(messageCreationRequest.getIdChatroom());
        Message message = new Message();
        message.setIdMessage("chat" + System.currentTimeMillis());
        message.setNguoigui(nguoiGui);
        message.setIdChatroom(chatroom);
        message.setNoidungtn(messageCreationRequest.getNoidungtn());
        message.setThoigiangui(new Date());
        Message savedMessage = messageRepository.save(message);

        // Nếu có fileUrl trong noidungtn, lưu attachment
        String noidungtn = messageCreationRequest.getNoidungtn();
        if (noidungtn != null && (noidungtn.startsWith("uploads/images/") || noidungtn.startsWith("uploads/files/"))) {
            Attachment attachment = new Attachment();
            attachment.setIdMessage(savedMessage);
            attachment.setFileUrl(noidungtn.startsWith("uploads/files/") ? noidungtn : null);
            attachment.setImgUrl(noidungtn.startsWith("uploads/images/") ? noidungtn : null);
            attachmentRepository.save(attachment);
        }

        return savedMessage;
    }

    public List<Message> findChatMessages(String chatId) throws Exception {
        Chatroom chatroom = chatRoomService.getChatroom(chatId);
        if(chatroom == null){
            throw new RuntimeException("Khong tim thay chat room " + chatId);
        }
        return messageRepository.findMessageByidChatroom(chatroom);
    }
}
