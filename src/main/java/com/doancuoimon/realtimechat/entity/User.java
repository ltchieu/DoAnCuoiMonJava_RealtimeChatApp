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

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @Nationalized
    @Column(name = "USERID", nullable = false, length = 20)
    private String userid;

    @Nationalized
    @Column(name = "USERNAME", length = 100)
    private String username;

    @Nationalized
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Nationalized
    @Column(name = "STATUS", length = 100)
    private int status;

    @Nationalized
    @Column(name = "NICKNAME", length = 300)
    private String nickname;

    @OneToMany(mappedBy = "idNguoinhan")
    private Set<ChatroomMember> chatroomMembers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "nguoigui")
    private Set<Message> messages = new LinkedHashSet<>();

}
