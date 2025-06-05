package com.doancuoimon.realtimechat.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatNofitication {
    String idChatroom;
    String noidungtn;
    String idNguoiGui;

    public ChatNofitication() {
    }

    public ChatNofitication(String idChatroom, String noidungtn, String idNguoiGui) {
        this.idChatroom = idChatroom;
        this.noidungtn = noidungtn;
        this.idNguoiGui = idNguoiGui;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", idChatroom, idNguoiGui, noidungtn);
    }
}
