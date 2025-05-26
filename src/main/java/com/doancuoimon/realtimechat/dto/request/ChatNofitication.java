package com.doancuoimon.realtimechat.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatNofitication {
    String idChatroom;
    String noidungtn;
    String idNguoiGui;
    String idNguoiNhan;

    public ChatNofitication() {
    }

    public ChatNofitication(String idChatroom, String noidungtn, String idNguoiGui, String idNguoiNhan) {
        this.idChatroom = idChatroom;
        this.noidungtn = noidungtn;
        this.idNguoiGui = idNguoiGui;
        this.idNguoiNhan = idNguoiNhan;
    }
}
