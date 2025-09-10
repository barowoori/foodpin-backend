package com.barowoori.foodpinbackend.member.command.application.dto;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

public class RequestMember {
    @Builder
    @Data
    public static class RegisterMemberDto{
        @Schema(description = "소셜 로그인 정보")
        @NotNull(message = "소셜 로그인 정보가 비었습니다")
        @Valid
        private CommonMember.SocialInfoDto socialInfoDto;
        @Schema(description = "핸드폰 번호", example = "01012345678")
        @NotEmpty(message = "핸드폰 번호가 비었습니다")
        private String phone;
        @Schema(description = "이메일")
        private String email;
        @Schema(description = "닉네임", example = "용감한 호랑이#1856")
        @NotEmpty(message = "닉네임이 비었습니다")
        private String nickname;

        public Member toEntity(){
            return Member.builder()
                    .phone(this.phone)
                    .email(this.email)
                    .socialLoginInfo(this.socialInfoDto.toEntity())
                    .nickname(this.nickname)
                    .build();
        }
    }

    @Builder
    @Data
    public static class RegisterTemporaryDto{
        @Schema(description = "소셜 로그인 정보")
        @NotNull(message = "소셜 로그인 정보가 비었습니다")
        @Valid
        private CommonMember.SocialInfoDto socialInfoDto;

        public Member toEntity(){
            return Member.builder()
                    .phone("temp")
                    .socialLoginInfo(this.socialInfoDto.toEntity())
                    .nickname("temp")
                    .build();
        }
    }

    @Builder
    @Data
    public static class LoginMemberRqDto{
        @Schema(description = "소셜 로그인 정보")
        @NotNull(message = "소셜 로그인 정보가 비었습니다")
        @Valid
        private CommonMember.SocialInfoDto socialInfoDto;
    }

    @Builder
    @Data
    public static class V2LoginMemberRqDto{
        @Schema(description = "소셜 로그인 정보")
        @NotNull(message = "소셜 로그인 정보가 비었습니다")
        @Valid
        private CommonMember.SocialInfoDto socialInfoDto;
        @Schema(description = "소셜 인증 토큰")
        @NotEmpty(message = "소셜 인증 토큰이 비었습니다.")
        private String identityToken;
        @Schema(description = "소셜 인증 코드")
        private String authorizationCode;
    }

    @Builder
    @Data
    public static class UpdateProfileRqDto{
        @Schema(description = "닉네임")
        @NotEmpty(message = "닉네임이 비었습니다")
        private String nickname;
        @Schema(description = "파일 아이디")
        private String image;
    }
}
