package com.barowoori.foodpinbackend.member.command.application.dto;

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

        public static LoginMemberRsDto toDto(String accessToken){
            return LoginMemberRsDto.builder()
                    .accessToken(accessToken)
                    .build();
        }
    }

    @Data
    @Builder
    public static class GetMemberDto{
        private SocialLoginInfo socialLoginInfo;
        @Schema(description = "핸드폰번호")
        private String phone;
        @Schema(description = "이메일")
        private String email;
        @Schema(description = "닉네임")
        private String nickname;

        public static GetMemberDto toDto(Member member){
            return GetMemberDto.builder()
                    .socialLoginInfo(member.getSocialLoginInfo())
                    .phone(member.getPhone())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .build();
        }
    }
}
