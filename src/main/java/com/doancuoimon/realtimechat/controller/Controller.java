/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.controller;

/**
 *
 * @author ADMIN
 */
import com.doancuoimon.realtimechat.dto.request.ChatroomCreationRequest;
import com.doancuoimon.realtimechat.dto.request.MessageCreationRequest;
import com.doancuoimon.realtimechat.dto.request.MessageRespone;
import com.doancuoimon.realtimechat.dto.request.UserCreationRequest;
import com.doancuoimon.realtimechat.entity.Chatroom;
import com.doancuoimon.realtimechat.entity.Message;
import com.doancuoimon.realtimechat.entity.User;
import com.doancuoimon.realtimechat.service.ChatRoomService;
import com.doancuoimon.realtimechat.service.MessageService;
import com.doancuoimon.realtimechat.service.UserService;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.messaging.simp.annotation.SendToUser;

@RestController
@RequiredArgsConstructor
public class Controller {

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private final UserService userService;
    @Autowired
    private final MessageService messageService;
    @Autowired
    private ChatRoomService chatRoomService;

    //region UserController
    @MessageMapping("/users.addUser")
    @SendToUser("/topic")
    public User addUser(@Payload UserCreationRequest user) {
        return userService.createUser(user);
    }

    @MessageMapping("/users.disconnectedUser")
    @SendTo("/user/topic")
    public User disconnectedUser(@Payload String username) {
        return userService.disconnected(username);
    }

    @GetMapping("/users")
    public List<UserCreationRequest> getConnectedUsers() {
        List<User> lst = userService.getConnectedUsers();
        List<UserCreationRequest> kq = new ArrayList<>();
        for (User u : lst) {
            UserCreationRequest user = new UserCreationRequest(u);
            kq.add(user);
        }
        return kq;
    }
    //endregion

    //region MessageController
    @GetMapping("/messages/{chatID}")
    public List<MessageRespone> getListMessages(
            @PathVariable("chatID") String chatId
    ) throws Exception {
        List<Message> lst = messageService.findChatMessages(chatId);
        List<MessageRespone> kq = new ArrayList<>();
        if(lst.isEmpty()){
            throw new RuntimeException("Khong co tin nhan nao " + chatId);
        }
        for(Message m : lst){
            MessageRespone mr = new MessageRespone(m);
            kq.add(mr);
        }
        return kq;
    }

    @MessageMapping("/chat")
    public void processMessage(
            @Payload MessageCreationRequest message,
            @Payload String chatId
    ) {
        Message newMessage = messageService.saveMessage(message, chatId);
//        messagingTemplate.convertAndSendToUser();
    }
    //endregion


    @MessageMapping("/chat.addChatromm")
    @SendTo("/chats/topic")
    public Chatroom addChatroom(@Payload ChatroomCreationRequest chatroomCreationRequest) {
        return chatRoomService.createChatRoom(chatroomCreationRequest);
    }
}
