package com.barowoori.foodpinbackend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberFcmInfoDto {
    private String memberId;
    private String fcmToken;
}
