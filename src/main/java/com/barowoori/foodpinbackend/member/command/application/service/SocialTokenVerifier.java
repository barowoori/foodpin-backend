package com.barowoori.foodpinbackend.member.command.application.service;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialTokenVerifier {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String verify(SocialLoginType type, String token) {
        try {
            return switch (type) {
                case KAKAO -> verifyKakaoToken(token);
                case APPLE -> verifyAppleToken(token);
                default -> throw new CustomException(MemberErrorCode.MEMBER_SOCIAL_TYPE_EMPTY);
            };
        } catch (Exception e) {
            log.error("[SocialTokenVerifier] 토큰 검증 실패", e);
            return null;
        }
    }

    // token : accessToken
    private String verifyKakaoToken(String token) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.get("id").asText();
        } else {
            throw new CustomException(MemberErrorCode.INVALID_IDENTITY_TOKEN);
        }
    }

    // token : identityToken
    private String verifyAppleToken(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new CustomException(MemberErrorCode.INVALID_IDENTITY_TOKEN);
        }

        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        JsonNode header = objectMapper.readTree(headerJson);
        String kid = header.get("kid").asText();

        String jwksUrl = "https://appleid.apple.com/auth/keys";
        ResponseEntity<String> keyResponse = restTemplate.getForEntity(jwksUrl, String.class);
        JsonNode keys = objectMapper.readTree(keyResponse.getBody()).get("keys");

        JsonNode matchedKey = null;
        for (JsonNode key : keys) {
            if (key.get("kid").asText().equals(kid)) {
                matchedKey = key;
                break;
            }
        }

        if (matchedKey == null) {
            throw new CustomException(MemberErrorCode.INVALID_IDENTITY_TOKEN);
        }

        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(matchedKey.get("n").asText()));
        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(matchedKey.get("e").asText()));
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update((parts[0] + "." + parts[1]).getBytes(StandardCharsets.UTF_8));

        boolean isValid = signature.verify(Base64.getUrlDecoder().decode(parts[2]));
        if (!isValid) {
            throw new CustomException(MemberErrorCode.INVALID_IDENTITY_TOKEN);
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        JsonNode payload = objectMapper.readTree(payloadJson);
        return payload.get("sub").asText();
    }
}
