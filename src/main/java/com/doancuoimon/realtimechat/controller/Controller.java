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
import com.doancuoimon.realtimechat.service.AttachmentService;
import com.doancuoimon.realtimechat.service.ChatRoomService;
import com.doancuoimon.realtimechat.service.MessageService;
import com.doancuoimon.realtimechat.service.UserService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

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
    @Autowired
    private AttachmentService attachmentService;

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

//    @GetMapping("/chatroom/{username}")
//    public ChatroomResponse getChatroomBetweenUsers(@PathVariable("username") String username,
//            Authentication authentication) {
//        return ChatroomResponse
//                .toDto(chatRoomService.findPrivateChatroomByUsernames(authentication.getName(), username), null);
//
//    }


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
        log.info("Message: {}", newMessage.getNoidungtn());
        ChatNofitication chatNofitication = new ChatNofitication(
                newMessage.getIdChatroom().getIdChatroom(),
                newMessage.getNoidungtn(),
                newMessage.getNguoigui().getNickname());
        String destination = "/topic/messages/" + message.getIdChatroom();
        messagingTemplate.convertAndSend(destination, chatNofitication);
    }
    // endregion

    // region Chatroom Controller
    @GetMapping("chatroom")
    public List<ChatroomResponse> getChatrooms(Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userService.getUserFromUserDetails(userService.loadUserByUsername(username));

        return chatRoomService.returnAvailableChatResponseForUser(currentUser);

    }

    @PostMapping("/chatroom")
    public Chatroom addChatroom(@RequestBody ChatroomCreationRequest chatroomCreationRequest) {
        return chatRoomService.createChatroom(chatroomCreationRequest);
    }

    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok().build();
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        log.info("User subscribed: {}", event.getUser());
        log.info("Subscription details: {}", event.getMessage());
    }

    @GetMapping("/chatroom/search/{tenchatroom}")
    public List<ChatroomResponse> searchUsers(@PathVariable("tenchatroom") String tenchatroom) {
        List<Chatroom> lst = chatRoomService.findChatroomByTenchatroom(tenchatroom);
        List<ChatroomResponse> kq = new ArrayList<>();
        for (Chatroom chatroom : lst) {
            kq.add(ChatroomResponse.toDto(chatroom, null));
        }
        return kq;
    }
    //endregion

    //region Attachment Controller
    @PostMapping("/upload")
    public ResponseEntity<AttachmentCreationRequest> uploadFile(
            @RequestParam("file") MultipartFile file) throws IOException {
        AttachmentCreationRequest attachment = attachmentService.storeAttachment(file);

        // Trả về URL tương đối
        String baseUrl = "/uploads/";
        if (attachment.getImageUrl() != null) {
            attachment.setImageUrl(baseUrl + "images/" + Paths.get(attachment.getImageUrl()).getFileName().toString());
            log.info("Image URL returned: {}", attachment.getImageUrl());
        }
        if (attachment.getFileUrl() != null) {
            attachment.setFileUrl(baseUrl + "files/" + Paths.get(attachment.getFileUrl()).getFileName().toString());
            log.info("File URL returned: {}", attachment.getFileUrl());
        }
        return ResponseEntity.ok(attachment);
    }
    //endregion
}
