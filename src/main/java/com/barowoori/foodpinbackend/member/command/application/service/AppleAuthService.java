package com.barowoori.foodpinbackend.member.command.application.service;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AppleAuthService {
    private final SocialTokenVerifier socialTokenVerifier;

    public AppleAuthService(SocialTokenVerifier socialTokenVerifier) {
        this.socialTokenVerifier = socialTokenVerifier;
    }

    public String makeCallBackRedirectURL(String authorizationCode, String token) {
        try {
            JsonNode payload = socialTokenVerifier.getAppleTokenPayload(token);
            String sub = payload.get("sub").asText();
            String email = payload.has("email") ? payload.get("email").asText() : "";

            //앱 리다이렉트
            return "signinwithapple?authorizationCode=" + authorizationCode +"&sub="+ sub + "&email="+ URLEncoder.encode(email, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new CustomException(MemberErrorCode.SOCIAL_LOGIN_DATA_PARSING_ERROR);
        }
    }
}
