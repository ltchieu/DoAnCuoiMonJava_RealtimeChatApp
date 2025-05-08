/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.entity;

/**
 *
 * @author ADMIN
 */
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ChatroomMemberId implements Serializable {
    private static final long serialVersionUID = -6008340531950327024L;

    @Column(name = "ID_CHATROOM", nullable = false, length = 20)
    private String idChatroom;

    @Column(name = "ID_NGUOINHAN", nullable = false)
    private String idNguoinhan;

    public ChatroomMemberId() {}

    public ChatroomMemberId(String idChatroom, String idNguoinhan) {
        this.idChatroom = idChatroom;
        this.idNguoinhan = idNguoinhan;
    }

    // Cần triển khai equals() và hashCode() cho khóa chính composite
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatroomMemberId that = (ChatroomMemberId) o;
        return Objects.equals(idChatroom, that.idChatroom) &&
                Objects.equals(idNguoinhan, that.idNguoinhan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idChatroom, idNguoinhan);
    }
}
