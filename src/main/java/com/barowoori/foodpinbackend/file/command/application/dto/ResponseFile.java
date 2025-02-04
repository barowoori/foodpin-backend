package com.barowoori.foodpinbackend.file.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

public class ResponseFile {
    @Data
    @Builder
    public static class FileId{
        @Schema(description = "파일 아이디")
        private String fileId;

        public static FileId of(String fileId){
            return FileId.builder()
                    .fileId(fileId)
                    .build();
        }
    }
}
