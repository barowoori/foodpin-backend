package com.barowoori.foodpinbackend.member.command.application.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    private String nickname;
    private MultipartFile image;
}
