package com.doancuoimon.realtimechat.dto.request;

import com.doancuoimon.realtimechat.entity.Message;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ADMIN
 */
@Getter
@Setter
public class MessageRespone implements Serializable{

    String noidungtn;
    String tenNguoiGui;
    String thoigianguitheogio;

    public MessageRespone(Message message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        noidungtn = message.getNoidungtn();
        tenNguoiGui = message.getNguoigui().getUsername();
        thoigianguitheogio = sdf.format(message.getThoigiangui());
    }
}
