/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.dto.request;

/**
 *
 * @author ADMIN
 */
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.examplechatapplication.chatapp.entity.Message}
 */
@Value
public class MessageCreationRequest implements Serializable {
    String idChatroom;
    String noidungtn;
}
