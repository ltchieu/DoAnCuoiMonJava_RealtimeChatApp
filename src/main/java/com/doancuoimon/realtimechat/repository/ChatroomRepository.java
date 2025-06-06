/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doancuoimon.realtimechat.repository;

/**
 *
 * @author ADMIN
 */
import com.doancuoimon.realtimechat.entity.Chatroom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatroomRepository extends JpaRepository<Chatroom, String> {
    @Query(value = """
            SELECT c.* FROM CHATROOM c
            JOIN chatroom_user cu ON c.ID_CHATROOM = cu.ID_CHATROOM
            WHERE cu.USER_ID IN (:senderId, :recipientId)
            GROUP BY c.ID_CHATROOM, c.NGAYLAP, c.ID_CHUDE, c.TENCHATROOM
            HAVING COUNT(DISTINCT cu.USER_ID) = 2
            """, nativeQuery = true)
    List<Chatroom> findChatroomByMembers(@Param("senderId") String senderId,
            @Param("recipientId") String recipientId);

    @Query("SELECT DISTINCT c FROM Chatroom c JOIN FETCH c.chatroomMembers WHERE :userId IN (SELECT u.userid FROM c.chatroomMembers u)")
    List<Chatroom> findAllByChatroomMembersUserid(String userId);

    @Query("SELECT c FROM Chatroom c WHERE LOWER(c.tenchatroom) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Chatroom> findChatroomByTenchatroom(@Param("keyword") String keyword);
}
