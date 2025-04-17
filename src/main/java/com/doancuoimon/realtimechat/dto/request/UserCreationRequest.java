/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.dto.request;

/**
 *
 * @author ADMIN
 */
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.examplechatapplication.chatapp.entity.User}
 */
@Getter
@Setter
public class UserCreationRequest implements Serializable {
    String userid;
    String username;
    String password;
    String status;
    String nickname;
}
