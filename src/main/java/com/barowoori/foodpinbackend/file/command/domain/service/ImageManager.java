package com.barowoori.foodpinbackend.file.command.domain.service;

import com.barowoori.foodpinbackend.file.infra.domain.ImageDirectory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface ImageManager {
    String updateFile(MultipartFile newFile, String oldFilePath, ImageDirectory imageDirectory);
    void deleteFile(String fileUrl);
    String getPreSignUrl(String fileUrl);
    File downloadFile(String fileUrl);
}
