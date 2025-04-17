/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.controller;

/**
 *
 * @author ADMIN
 */
import com.doancuoimon.realtimechat.dto.request.UserCreationRequest;
import com.doancuoimon.realtimechat.entity.User;
import com.doancuoimon.realtimechat.service.MessageService;
import com.doancuoimon.realtimechat.service.UserService;
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


@RestController
@RequiredArgsConstructor
public class Controller {

    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private final UserService userService;
    private final MessageService messageService;

    //region UserController
    @MessageMapping("/users.addUser")
    @SendTo("/user/topic")
    public User addUser(@Payload UserCreationRequest user){
        return userService.createUser(user);
    }

    @MessageMapping("/users.disconnectedUser")
    @SendTo("/user/topic")
    public User disconnectedUser(@Payload String username){
        return userService.disconnected(username);
    }

    @GetMapping("/users")
    public List<User> getConnectedUsers() {
        return userService.getConnectedUsers();
    }
    //endregion

    //region MessageController
//    @GetMapping("/messages/{chatID}")
//    public List<Message> getListMessages(
//            @PathVariable("chatID") String chatId
//    ) {
//       return messageService.findChatMessages(chatId);
//    }
//
//    public void processMessage(
//            @Payload MessageCreationRequest message,
//            @Payload ChatRoomCreationRequest chatRoomCreationRequest,
//            @Payload String chatId
//    )
//    {
//        Message newMessage = messageService.saveMessage(message, chatRoomCreationRequest, chatId);
//
//        messagingTemplate.convertAndSendToUser(, "/queue/messages", newMessage);        
//    }
    //endregion
}

