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
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "MAUNEN")
public class BackgroundColor {
    @Id
    @Nationalized
    @Column(name = "ID_MAUNEN", nullable = false, length = 20)
    private String idMaunen;

    @Nationalized
    @Column(name = "TENMAUNEN", length = 200)
    private String tenmaunen;
}

