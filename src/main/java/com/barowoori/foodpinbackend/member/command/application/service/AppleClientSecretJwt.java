package com.barowoori.foodpinbackend.member.command.application.service;

import com.barowoori.foodpinbackend.config.oauth.OAuthConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AppleClientSecretJwt {

    private final OAuthConfig oauth;

    public String create() {
        try {
            PrivateKey key = readPrivateKey(oauth.getApple().getPrivateKeyPem());
            long now = Instant.now().getEpochSecond();
            long exp = now + 60L * 60 * 24 * 180; // 최대 6개월

            return Jwts.builder()
                    .setHeaderParam("kid", oauth.getApple().getKeyId())
                    .setHeaderParam("alg", "ES256")
                    .setIssuer(oauth.getApple().getTeamId())                          // iss
                    .setIssuedAt(new Date(now * 1000))         // iat
                    .setExpiration(new Date(exp * 1000))       // exp
                    .setAudience("https://appleid.apple.com")  // aud
                    .setSubject(oauth.getApple().getClientId())                      // sub (bundle id)
                    .signWith(key, SignatureAlgorithm.ES256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Failed to build Apple client secret JWT", e);
        }
    }

    private PrivateKey readPrivateKey(String pem) throws Exception {
        String normalized = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("EC").generatePrivate(spec);
    }
}
