package com.barowoori.foodpinbackend.member.command.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Embeddable
@AllArgsConstructor
public class SocialLoginInfo {
    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_login_type")
    private SocialLoginType type;

    @Column(name = "social_login_id")
    private String id;

    @Column(name = "apple_refresh_token")
    private String appleRefreshToken;

    protected SocialLoginInfo(){

    }
    public SocialLoginInfo(SocialLoginType type, String id) {
        this.type = type;
        this.id = id;
    }

    public void setAppleRefreshToken(String appleRefreshToken) {
        this.appleRefreshToken = appleRefreshToken;
    }
}
