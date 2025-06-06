package com.doancuoimon.realtimechat.repository;

import com.doancuoimon.realtimechat.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
}