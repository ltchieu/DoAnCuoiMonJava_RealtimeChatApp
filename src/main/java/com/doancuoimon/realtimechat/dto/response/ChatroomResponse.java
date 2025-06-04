package com.doancuoimon.realtimechat.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.doancuoimon.realtimechat.entity.Chatroom;
import com.doancuoimon.realtimechat.entity.User;

public record ChatroomResponse(
    String idChatroom,
    LocalDate ngaylap,
    String tenchatroom,
    List<String> chatroomMemberIds
) { 
    public static ChatroomResponse toDto(Chatroom entity) {
        return new ChatroomResponse(
            entity.getIdChatroom(),
            entity.getNgaylap(),
            entity.getTenchatroom(),
            entity.getChatroomMembers().stream()
                .map(User::getUserid)
                .toList()
        );
    }
}
