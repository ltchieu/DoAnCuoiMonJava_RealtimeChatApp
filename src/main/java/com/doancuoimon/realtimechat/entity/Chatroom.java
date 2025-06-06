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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "CHATROOM")
public class Chatroom {
    @Id
    @Nationalized
    @Column(name = "ID_CHATROOM", nullable = false, length = 20)
    private String idChatroom;

    @Column(name = "NGAYLAP")
    private LocalDate ngaylap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CHUDE")
    private BackgroundColor idChude;

    @OneToMany(mappedBy = "idChatroom")
    private List<Message> messages = new ArrayList<>();

    @Nationalized
    @Column(name = "TENCHATROOM", length = 200)
    private String tenchatroom;

    @ManyToMany
    @JoinTable(name = "chatroom_user", joinColumns = @JoinColumn(name = "ID_CHATROOM"), inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "USERID"))
    private List<User> chatroomMembers = new ArrayList<>();

}
