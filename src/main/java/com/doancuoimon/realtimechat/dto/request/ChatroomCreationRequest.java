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
import com.doancuoimon.realtimechat.entity.ChatroomMember;
import com.doancuoimon.realtimechat.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatroomCreationRequest {
    private String tenchatroom;
    private BackgroundColor idChuDe;
    @JsonProperty("NguoiNhans")
    private List<User> NguoiNhans = new ArrayList<>();
}
