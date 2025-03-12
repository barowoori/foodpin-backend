package com.barowoori.foodpinbackend.member.command.application.dto;

import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

public class ResponseMember {
    @Data
    @Builder
    public static class LoginMemberRsDto{
        @Schema(description = "액세스 토큰")
        private String accessToken;
        @Schema(description = "리프레쉬 토큰")
        private String refreshToken;

        public static LoginMemberRsDto toDto(String accessToken, String refreshToken){
            return LoginMemberRsDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }

    @Data
    @Builder
    public static class ReissueTokenDto{
        @Schema(description = "액세스 토큰")
        private String accessToken;
        @Schema(description = "리프레쉬 토큰")
        private String refreshToken;

        public static ReissueTokenDto toDto(String accessToken, String refreshToken){
            return ReissueTokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }

    @Data
    @Builder
    public static class GetInfoDto{
        @Schema(description = "소셜 로그인 정보")
        private CommonMember.SocialInfoDto socialInfoDto;
        @Schema(description = "휴대폰 번호")
        private String phone;
        @Schema(description = "이메일")
        private String email;
        @Schema(description = "닉네임")
        private String nickname;
        @Schema(description = "이미지")
        private String image;

        public static GetInfoDto toDto(Member member, ImageManager imageManager){
            return GetInfoDto.builder()
                    .socialInfoDto(CommonMember.SocialInfoDto.toDto(member.getSocialLoginInfo()))
                    .phone(member.getPhone())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .image(member.getImage() != null ? member.getImage().getPreSignUrl(imageManager) : null)
                    .build();
        }
    }

    @Data
    @Builder
    public static class GenerateNicknameDto{
        @Schema(description = "생성된 닉네임")
        private String nickname;

        public static GenerateNicknameDto toDto(String nickname){
            return GenerateNicknameDto.builder()
                    .nickname(nickname)
                    .build();
        }
    }

    @Data
    @Builder
    public static class CheckNicknameDto{
        @Schema(description = "닉네임 사용 가능 여부")
        private Boolean isUsable;

        public static CheckNicknameDto toDto(Boolean isUsable){
            return CheckNicknameDto.builder()
                    .isUsable(isUsable)
                    .build();
        }
    }

    @Data
    @Builder
    public static class CheckPhoneDto{
        @Schema(description = "소셜 로그인 정보")
        private CommonMember.SocialInfoDto socialInfoDto;

        public static CheckPhoneDto toDto(SocialLoginInfo socialLoginInfo){
            return CheckPhoneDto.builder()
                    .socialInfoDto(CommonMember.SocialInfoDto.toDto(socialLoginInfo))
                    .build();
        }
    }
}
