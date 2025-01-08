package com.barowoori.foodpinbackend.member.command.application.dto;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.MemberType;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import lombok.Builder;
import lombok.Data;

public class RequestMember {
    @Builder
    @Data
    public static class RegisterMemberDto{
        private SocialLoginInfo socialLoginInfo;
        private String phone;
        private String nickname;

        public static Member toEntity(RegisterMemberDto registerMemberDto){
            return Member.builder()
                    .phone(registerMemberDto.getPhone())
                    .socialLoginInfo(registerMemberDto.getSocialLoginInfo())
                    .nickname(registerMemberDto.getNickname())
                    .build();
        }
    }

    @Builder
    @Data
    public static class LoginMemberRqDto{
        private SocialLoginInfo socialLoginInfo;
    }
}
