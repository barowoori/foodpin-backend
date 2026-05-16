package com.barowoori.foodpinbackend.member.command.application.dto;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchBackOfficeMemberDto {
    @Schema(description = "회원 ID")
    private String memberId;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "휴대폰 번호")
    private String phone;
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "소셜 로그인 타입")
    private SocialLoginType socialLoginType;

    public static SearchBackOfficeMemberDto toDto(Member member) {
        return SearchBackOfficeMemberDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .phone(member.getPhone())
                .email(member.getEmail())
                .socialLoginType(member.getSocialLoginInfo() != null ? member.getSocialLoginInfo().getType() : null)
                .build();
    }
}
