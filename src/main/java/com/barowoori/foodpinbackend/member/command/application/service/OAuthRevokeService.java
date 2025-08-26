package com.barowoori.foodpinbackend.member.command.application.service;

import com.barowoori.foodpinbackend.config.oauth.OAuthConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuthRevokeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final OAuthConfig oauth;
    private final AppleClientSecretJwt appleClientSecretJwt;

    public void revokeAppleAccess(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", oauth.getApple().getClientId());
        form.add("client_secret", appleClientSecretJwt.create());
        form.add("token", refreshToken);
        form.add("token_type_hint", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        restTemplate.postForEntity("https://appleid.apple.com/auth/revoke",
                new HttpEntity<>(form, headers), String.class);
    }

    public void unlinkKakaoAccess(String kakaoId) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("target_id_type", "user_id");
        form.add("target_id", kakaoId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + oauth.getKakao().getAdminKey());

        restTemplate.postForEntity("https://kapi.kakao.com/v1/user/unlink",
                new HttpEntity<>(form, headers), String.class);
    }
}