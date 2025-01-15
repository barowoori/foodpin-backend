package com.barowoori.foodpinbackend.member.command.application.dto;

import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

public class CommonMember {
    @Data
    @Builder
    public static class SocialInfoDto{
        @Schema(description = "소셜 로그인 종류")
        private SocialLoginType type;
        @Schema(description = "소셜 로그인 ID")
        private String id;

        public static SocialInfoDto toDto(SocialLoginInfo socialLoginInfo){
            return SocialInfoDto.builder()
                    .type(socialLoginInfo.getType())
                    .id(socialLoginInfo.getId())
                    .build();
        }

        public static SocialLoginInfo toEntity(SocialInfoDto socialInfoDto){
            return SocialLoginInfo.builder()
                    .type(socialInfoDto.getType())
                    .id(socialInfoDto.getId())
                    .build();
        }
    }
}
