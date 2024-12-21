package com.barowoori.foodpinbackend.member.command.domain.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageManager {
    String updateProfile(MultipartFile image);
    void deleteProfile(String path);
}
