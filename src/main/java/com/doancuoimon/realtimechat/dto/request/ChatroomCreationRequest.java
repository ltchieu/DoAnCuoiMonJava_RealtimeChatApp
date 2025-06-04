/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.dto.request;

/**
 *
 * @author ADMIN
 */

import com.doancuoimon.realtimechat.entity.BackgroundColor;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatroomCreationRequest {
    private String tenchatroom;
    private BackgroundColor idChuDe;
    private List<String> usernameNguoiNhans = new ArrayList<>();
}
