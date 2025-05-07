package com.doancuoimon.realtimechat.repository;

import com.doancuoimon.realtimechat.entity.ChatroomMember;
import com.doancuoimon.realtimechat.entity.ChatroomMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomMemberRepository extends JpaRepository<ChatroomMember, ChatroomMemberId> {
}