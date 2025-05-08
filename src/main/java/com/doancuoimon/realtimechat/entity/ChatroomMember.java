/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.entity;

/**
 *
 * @author ADMIN
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "CHATROOM_MEMBER", schema = "dbo")
public class ChatroomMember {
    @EmbeddedId
    private ChatroomMemberId id;

    @MapsId("idChatroom")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CHATROOM", nullable = false, referencedColumnName = "ID_CHATROOM")
    @JsonIgnore
    private Chatroom idChatroom;

    @Column(name = "NGAYTHAMGIA")
    private Instant ngaythamgia;

    @Column(name = "NGAYROIDI")
    private Instant ngayroidi;

    @MapsId("idNguoinhan")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_NGUOINHAN", nullable = false)
    @JsonIgnore
    private User idNguoinhan;

    public ChatroomMember() {}

    public ChatroomMember(Chatroom chatroom, User user, Instant ngaythamgia, ChatroomMemberId id) {
        this.idChatroom = chatroom;
        this.idNguoinhan = user;
        this.ngaythamgia = ngaythamgia;
        this.id = id;
    }
}
