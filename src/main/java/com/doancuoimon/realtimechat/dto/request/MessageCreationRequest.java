/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.dto.request;

/**
 *
 * @author ADMIN
 */
import com.doancuoimon.realtimechat.entity.Chatroom;
import com.doancuoimon.realtimechat.entity.User;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.examplechatapplication.chatapp.entity.Message}
 */
@Value
public class MessageCreationRequest implements Serializable {
    String idMessage;
    Chatroom idChatroom;
    String noidungtn;
    User nguoigui;
    Instant thoigiangui;
}
