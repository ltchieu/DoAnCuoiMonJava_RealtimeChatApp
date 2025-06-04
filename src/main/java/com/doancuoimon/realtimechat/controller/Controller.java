/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.controller;

/**
 * @author ADMIN
 */

import com.doancuoimon.realtimechat.dto.request.*;
import com.doancuoimon.realtimechat.dto.response.ChatroomResponse;
import com.doancuoimon.realtimechat.dto.response.UserResponse;
import com.doancuoimon.realtimechat.entity.Chatroom;
import com.doancuoimon.realtimechat.entity.Message;
import com.doancuoimon.realtimechat.entity.User;
import com.doancuoimon.realtimechat.entity.UserDetailsImpl;
import com.doancuoimon.realtimechat.service.ChatRoomService;
import com.doancuoimon.realtimechat.service.MessageService;
import com.doancuoimon.realtimechat.service.UserService;

import java.util.ArrayList;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;

@Slf4j
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

    // region UserController
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
    public List<UserResponse> getConnectedUsers(Authentication authentication) {
        UserDetailsImpl userContext = (UserDetailsImpl) userService.loadUserByUsername(authentication.getName());
        User authUser = userContext.user();
        List<User> lst = userService.getConnectedUsers();

        List<UserResponse> kq = new ArrayList<>();
        for (User u : lst) {
            if (u.getUsername().equals(authUser.getUsername()))
                continue;
            kq.add(UserResponse.toDto(u));
        }
        return kq;
    }

    @GetMapping("/chatroom/{username}")
    public ChatroomResponse getChatroomBetweenUsers(@PathVariable("username") String username, Authentication authentication) {
        return ChatroomResponse
                .toDto(chatRoomService.findPrivateChatroomByUsernames(authentication.getName(), username));

    }
    // endregion

    // region MessageController
    @GetMapping("/messages/{chatID}")
    public List<MessageRespone> getListMessages(
            @PathVariable("chatID") String chatId) throws Exception {
        List<Message> lst = messageService.findChatMessages(chatId);
        List<MessageRespone> kq = new ArrayList<>();
        for (Message m : lst) {
            MessageRespone mr = new MessageRespone(m);
            kq.add(mr);
        }
        return kq;
    }

    @MessageMapping("/chat")
    @Transactional
    public void processMessage(
            @Payload MessageCreationRequest message,
            Authentication authentication) {
        User nguoiGui = userService.getUserFromUserDetails(userService.loadUserByUsername(authentication.getName()));
        Message newMessage = messageService.saveMessage(message, nguoiGui);
        log.info("Message {}", newMessage.getNoidungtn());
        List<User> lstIdNguoiNhan = chatRoomService.getChatroomMembers(newMessage.getIdChatroom().getIdChatroom());
        for (User u : lstIdNguoiNhan) {
            ChatNofitication chatNofitication = new ChatNofitication(
                    newMessage.getIdChatroom().getIdChatroom(),
                    newMessage.getNoidungtn(),
                    newMessage.getNguoigui().getUserid(),
                    u.getUsername());
            String destination = "/queue/messages/" + message.getIdChatroom();
            log.info("destination {} length {}", destination, destination.length());
            messagingTemplate.convertAndSend(destination, chatNofitication);
        }

    }
    // endregion

    @PostMapping("/chatroom")
    public Chatroom addChatroom(@RequestBody ChatroomCreationRequest chatroomCreationRequest,
            Authentication authentication) {
        UserDetailsImpl userContext = (UserDetailsImpl) userService.loadUserByUsername(authentication.getName());
        return chatRoomService.createChatroom(chatroomCreationRequest, userContext.user());
    }
}
