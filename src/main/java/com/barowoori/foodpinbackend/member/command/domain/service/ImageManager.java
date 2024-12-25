package com.barowoori.foodpinbackend.member.command.domain.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageManager {
    String updateProfile(MultipartFile image, String oldFilePath);
    void deleteFile(String fileUrl);
}
