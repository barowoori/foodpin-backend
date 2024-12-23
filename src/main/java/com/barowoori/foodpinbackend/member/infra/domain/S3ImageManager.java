package com.barowoori.foodpinbackend.member.infra.domain;

import com.barowoori.foodpinbackend.member.command.domain.service.ImageManager;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3ImageManager implements ImageManager {
    @Override
    public String updateProfile(MultipartFile image){
        String path ="s3 Path";
        return path;
    }
    @Override
    public void deleteProfile(String path){

    }
}
