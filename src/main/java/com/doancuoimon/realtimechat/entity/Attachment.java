package com.doancuoimon.realtimechat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "ATTACHMENT")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ATTACH", nullable = false)
    private Integer idAttach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MESSAGE")
    private Message idMessage;

    @Nationalized
    @Column(name = "FILE_URL", length = 200)
    private String fileUrl;

    @Nationalized
    @Column(name = "IMG_URL", length = 200)
    private String imgUrl;

    @Column(name = "CREATE_AT")
    private Instant createAt;

}