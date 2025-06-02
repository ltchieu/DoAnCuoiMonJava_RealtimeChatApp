/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.dto.request;

/**
 *
 * @author ADMIN
 */
import com.doancuoimon.realtimechat.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.doancuoimon.realtimechat.entity.User}
 */
@Getter
@Setter
public class UserCreationRequest implements Serializable {
    String username;
    String password;
    String nickname;

    public UserCreationRequest() {
    }

    public UserCreationRequest(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
    }
    
    
}
