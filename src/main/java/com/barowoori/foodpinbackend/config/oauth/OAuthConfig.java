package com.barowoori.foodpinbackend.config.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties(prefix = "oauth")
@PropertySource(value = "classpath:/secrets/oauth-key.properties")
@Configuration
@Getter
@Setter
public class OAuthConfig {

    private Apple apple;
    private Kakao kakao;

    @Getter
    @Setter
    public static class Apple {
        private String teamId;
        private String keyId;
        private String clientId;
        private String privateKeyPem;
    }

    @Getter
    @Setter
    public static class Kakao {
        private String adminKey;
    }
}