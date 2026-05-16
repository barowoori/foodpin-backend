package com.barowoori.foodpinbackend.member.command.application.dto;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.MemberType;
import com.barowoori.foodpinbackend.member.command.domain.model.ServiceType;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class ResponseBackOfficeMember {

    @Data
    @Builder
    public static class GetBackOfficeMemberListDto {
        @Schema(description = "회원 ID")
        private String memberId;
        @Schema(description = "가입 일시")
        private LocalDateTime createdAt;
        @Schema(description = "휴대폰 번호")
        private String phone;
        @Schema(description = "이메일")
        private String email;
        @Schema(description = "닉네임")
        private String nickname;
        @Schema(description = "회원 권한")
        private Set<MemberType> memberTypes;
        @Schema(description = "소셜 로그인 타입")
        private SocialLoginType socialLoginType;
        @Schema(description = "서비스 유형")
        private ServiceType serviceType;
        @Schema(description = "탈퇴 여부")
        private Boolean isDeleted;

        public static GetBackOfficeMemberListDto toDto(Member member) {
            return GetBackOfficeMemberListDto.builder()
                    .memberId(member.getId())
                    .createdAt(member.getCreatedAt())
                    .phone(member.getPhone())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .memberTypes(member.getTypes())
                    .socialLoginType(member.getSocialLoginInfo() != null ? member.getSocialLoginInfo().getType() : null)
                    .serviceType(member.getServiceType())
                    .isDeleted(member.getIsDeleted())
                    .build();
        }
    }

    public enum SignupStatisticsUnit {
        DAY, WEEK, MONTH
    }

    @Data
    @Builder
    public static class SignupStatisticsSummaryDto {
        @Schema(description = "회원 가입 수")
        private Long memberSignupCount;
        @Schema(description = "비회원 가입 수")
        private Long guestSignupCount;
        @Schema(description = "총 가입 수")
        private Long totalSignupCount;
    }

    @Data
    @Builder
    public static class TodaySignupStatisticsDto {
        @Schema(description = "집계 날짜")
        private LocalDate date;
        @Schema(description = "회원 가입 수")
        private Long memberSignupCount;
        @Schema(description = "비회원 가입 수")
        private Long guestSignupCount;
        @Schema(description = "총 가입 수")
        private Long totalSignupCount;
    }

    @Data
    @Builder
    public static class SignupStatisticsBucketDto {
        @Schema(description = "집계 시작일")
        private LocalDate startDate;
        @Schema(description = "집계 종료일")
        private LocalDate endDate;
        @Schema(description = "회원 가입 수")
        private Long memberSignupCount;
        @Schema(description = "비회원 가입 수")
        private Long guestSignupCount;
        @Schema(description = "총 가입 수")
        private Long totalSignupCount;
    }

    @Data
    @Builder
    public static class BackOfficeSignupStatisticsDto {
        @Schema(description = "조회 시작일")
        private LocalDate from;
        @Schema(description = "조회 종료일")
        private LocalDate to;
        @Schema(description = "집계 단위")
        private SignupStatisticsUnit unit;
        @Schema(description = "기간 요약")
        private SignupStatisticsSummaryDto summary;
        @Schema(description = "구간별 통계")
        private List<SignupStatisticsBucketDto> buckets;
    }
}
