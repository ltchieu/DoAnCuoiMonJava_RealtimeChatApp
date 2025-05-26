/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.entity;

/**
 *
 * @author ADMIN
 */
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "MESSAGE")
public class Message {
    @Id
    @Nationalized
    @Column(name = "ID_MESSAGE", nullable = false, length = 100)
    private String idMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CHATROOM")
    private Chatroom idChatroom;

    @Nationalized
    @Lob
    @Column(name = "NOIDUNGTN")
    private String noidungtn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NGUOIGUI")
    private User nguoigui;

    @Column(name = "THOIGIANGUI")
    private Date thoigiangui;

}
