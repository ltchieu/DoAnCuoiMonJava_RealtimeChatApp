package com.doancuoimon.realtimechat.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentCreationRequest {
    String fileUrl;
    String imageUrl;
    String originalFilename;
}
