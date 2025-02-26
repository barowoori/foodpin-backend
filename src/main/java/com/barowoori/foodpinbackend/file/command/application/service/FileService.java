package com.barowoori.foodpinbackend.file.command.application.service;

import com.barowoori.foodpinbackend.file.command.application.dto.ResponseFile;
import com.barowoori.foodpinbackend.file.command.domain.model.File;
import com.barowoori.foodpinbackend.file.command.domain.repository.FileRepository;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.file.infra.domain.ImageDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final ImageManager imageManager;

    public ResponseFile.FileId upload(MultipartFile multipartFile) {
        String path = imageManager.updateFile(multipartFile, null, ImageDirectory.DEFAULT);
        File file = fileRepository.save(File.builder().path(path).build());
        return ResponseFile.FileId.builder().fileId(file.getId()).build();
    }
}
