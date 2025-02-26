package com.barowoori.foodpinbackend.member.command.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Embeddable
public class SocialLoginInfo {
    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_login_type")
    private SocialLoginType type;

    @Column(name = "social_login_id")
    private String id;

    protected SocialLoginInfo(){

    }
    public SocialLoginInfo(SocialLoginType type, String id) {
        this.type = type;
        this.id = id;
    }


}
