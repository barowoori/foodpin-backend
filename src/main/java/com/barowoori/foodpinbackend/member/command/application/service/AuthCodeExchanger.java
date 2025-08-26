package com.barowoori.foodpinbackend.member.command.application.service;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.config.oauth.OAuthConfig;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthCodeExchanger {

    private final RestTemplate restTemplate = new RestTemplate();
    private final OAuthConfig oauth;
    private final AppleClientSecretJwt appleClientSecretJwt;

    public String exchangeToken(String authorizationCode) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", authorizationCode);
        form.add("client_id", oauth.getApple().getClientId());
        form.add("client_secret", appleClientSecretJwt.create());
        form.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<tokenResponse> response = restTemplate.postForEntity(
                    "https://appleid.apple.com/auth/token",
                    new HttpEntity<>(form, headers),
                    tokenResponse.class
            );
            tokenResponse body = response.getBody();

            if (body == null || body.refresh_token == null) {
                throw new IllegalStateException("Token exchange failed: empty body");
            }
            return body.refresh_token;
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            if (responseBody.contains("invalid_grant")) {
                throw new CustomException(MemberErrorCode.AUTH_CODE_EXPIRED);
            }
            throw e;
        }
    }

    @Getter
    private static class tokenResponse {
        public String access_token;
        public String token_type;
        public long expires_in;
        public String refresh_token;
        public String id_token;
    }
}
