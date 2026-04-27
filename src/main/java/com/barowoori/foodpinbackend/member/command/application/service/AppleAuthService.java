package com.barowoori.foodpinbackend.member.command.application.service;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import org.springframework.stereotype.Service;


@Service
public class AppleAuthService {
    private final SocialTokenVerifier socialTokenVerifier;

    public AppleAuthService(SocialTokenVerifier socialTokenVerifier) {
        this.socialTokenVerifier = socialTokenVerifier;
    }

    public String makeCallBackRedirectURL(String authorizationCode, String token, String state) {
        try {
            if ("web".equals(state)) {
                // 웹으로 리다이렉트
                return "https://www.barowoori.click/auth/apple/callback?code="
                        + authorizationCode + "&id_token=" + token;
            }

            // 앱
            return "com.barowoori.foodpin.signinwithapple://callback?code="
                    + authorizationCode + "&id_token=" + token;

        } catch (Exception e) {
            throw new CustomException(MemberErrorCode.SOCIAL_LOGIN_DATA_PARSING_ERROR);
        }
    }
}
