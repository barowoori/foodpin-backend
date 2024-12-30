package com.barowoori.foodpinbackend.member.command.domain.service;

import com.barowoori.foodpinbackend.member.infra.domain.ImageDirectory;
import org.springframework.web.multipart.MultipartFile;

public interface ImageManager {
    String updateFile(MultipartFile newFile, String oldFilePath, ImageDirectory imageDirectory);
    void deleteFile(String fileUrl);
}
