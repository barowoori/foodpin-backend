package com.barowoori.foodpinbackend.member.command.application.dto;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.MemberType;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

public class RequestMember {
    @Builder
    @Data
    public static class RegisterMemberDto{
        private SocialLoginInfo socialLoginInfo;
        @NotEmpty(message = "핸드폰 번호가 비었습니다")
        private String phone;
        @NotEmpty(message = "닉네임이 비었습니다")
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
