package com.barowoori.foodpinbackend.file.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.file.command.application.dto.ResponseFile;
import com.barowoori.foodpinbackend.file.command.application.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "파일 API", description = "파일 API")
@RequiredArgsConstructor
@RequestMapping("/api/files")
@RestController
public class FileController {
    private final FileService fileService;

    @Operation(summary = "파일 저장", description = "파일 업로드 시 파일 아이디 반환")
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<ResponseFile.FileId>> uploadFile(@Valid @RequestPart(name = "file") MultipartFile file){
        ResponseFile.FileId response = fileService.upload(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ResponseFile.FileId>builder()
                        .data(response).build());
    }
}
