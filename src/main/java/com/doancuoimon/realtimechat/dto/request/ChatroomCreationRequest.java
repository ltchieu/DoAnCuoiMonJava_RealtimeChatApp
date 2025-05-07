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
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ChatroomCreationRequest {
    private LocalDate ngayTao;
    private String tenchatroom;
    private BackgroundColor idChuDe;
    private List<String> idNguoiNhan;
}
