package com.barowoori.foodpinbackend.member.command.application.requestDto;

import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequest {
    private String name;
    private String phone;
    private String email;
    private String nickname;
    private SocialLoginType socialLoginType;
    private String socialLoginId;
}
