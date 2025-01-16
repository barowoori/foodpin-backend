package com.barowoori.foodpinbackend.member.command.application.dto;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.MemberType;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

public class RequestMember {
    @Builder
    @Data
    public static class RegisterMemberDto{
        @Schema(description = "소셜 로그인 정보")
        @NotEmpty(message = "소셜 로그인 정보가 비었습니다")
        private CommonMember.SocialInfoDto socialInfoDto;
        @Schema(description = "핸드폰 번호", example = "01012345678")
        @NotEmpty(message = "핸드폰 번호가 비었습니다")
        private String phone;
        @Schema(description = "닉네임", example = "용감한 호랑이#1856")
        @NotEmpty(message = "닉네임이 비었습니다")
        private String nickname;

        public Member toEntity(){
            return Member.builder()
                    .phone(this.phone)
                    .socialLoginInfo(this.socialInfoDto.toEntity())
                    .nickname(this.nickname)
                    .build();
        }
    }

    @Builder
    @Data
    public static class LoginMemberRqDto{
        @Schema(description = "소셜 로그인 정보")
        @NotEmpty(message = "소셜 로그인 정보가 비었습니다")
        private CommonMember.SocialInfoDto socialInfoDto;
    }

    @Builder
    @Data
    public static class UpdateProfileRqDto{
        @Schema(description = "닉네임")
        @NotEmpty(message = "닉네임이 비었습니다")
        private String nickname;
        @Schema(description = "기존 이미지 경로")
        private String originImageUrl;
    }
}
