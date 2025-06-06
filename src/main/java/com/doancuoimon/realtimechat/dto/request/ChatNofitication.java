package com.doancuoimon.realtimechat.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatNofitication {
    String idChatroom;
    String noidungtn;
    String tenNguoiGui;

    public ChatNofitication() {
    }

    public ChatNofitication(String idChatroom, String noidungtn, String tenNguoiGui) {
        this.idChatroom = idChatroom;
        this.noidungtn = noidungtn;
        this.tenNguoiGui = tenNguoiGui;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", idChatroom, tenNguoiGui, noidungtn);
    }
}
