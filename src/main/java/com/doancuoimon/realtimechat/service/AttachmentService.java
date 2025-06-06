package com.doancuoimon.realtimechat.service;

import com.doancuoimon.realtimechat.dto.request.AttachmentCreationRequest;
import com.doancuoimon.realtimechat.entity.Attachment;
import com.doancuoimon.realtimechat.entity.Message;
import com.doancuoimon.realtimechat.repository.AttachmentRepository;
import com.doancuoimon.realtimechat.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AttachmentService {
    @Autowired
    private AttachmentRepository attachmentRepository;

    private static final String UPLOAD_DIR = "uploads/";
    private static final String IMAGE_DIR = UPLOAD_DIR + "images/";
    private static final String FILE_DIR = UPLOAD_DIR + "files/";
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");

    public AttachmentCreationRequest storeAttachment(MultipartFile file) throws IOException {
        if(file.isEmpty()) {
            throw new IOException("File is empty");
        }

        //Lấy ra tên file gốc và phần mở rộng
        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null) {
            throw new IOException("Invalid file name");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        String uniqueFileName = originalFilename.substring(0, originalFilename.lastIndexOf(".") - 1) + UUID.randomUUID().toString() + "." + extension;

        //Xác định thư mục lưu trữ file
        String targetDir = IMAGE_EXTENSIONS.contains(extension) ? IMAGE_DIR : FILE_DIR;
        Path targetPath = Paths.get(targetDir + uniqueFileName).toAbsolutePath().normalize();

        //Tạo thư mục nếu chưa tồn tại
        Files.createDirectories(targetPath.getParent());

        //Lưu vào thư mục uploads
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        log.info("File save to: {}", targetPath);

        AttachmentCreationRequest attachment = new AttachmentCreationRequest();
        attachment.setOriginalFilename(originalFilename);
        if(IMAGE_EXTENSIONS.contains(extension)){
            attachment.setImageUrl(IMAGE_DIR + uniqueFileName);
            attachment.setFileUrl(null);
            log.info("Image URL returned: {}", attachment.getImageUrl());
        }
        else{
            attachment.setFileUrl(FILE_DIR + uniqueFileName);
            attachment.setImageUrl(null);
            log.info("File URL returned: {}", attachment.getFileUrl());
        }

        return attachment;
    }
}
