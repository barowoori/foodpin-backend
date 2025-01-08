package com.barowoori.foodpinbackend.member.command.application.dto;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import lombok.Builder;
import lombok.Data;

public class ResponseMember {
    @Data
    @Builder
    public static class LoginMemberRsDto{
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
        private String phone;
        private String email;
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
