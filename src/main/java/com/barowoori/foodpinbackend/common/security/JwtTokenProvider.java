package com.barowoori.foodpinbackend.common.security;

import com.barowoori.foodpinbackend.member.command.domain.model.MemberType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@PropertySource("classpath:/secrets/jwt-secret-key.properties")
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService; // Spring Security 에서 제공하는 서비스 레이어

    @Value("${jwt.secret}")
    private String secretKey;

    private final long accessTokenValidMillisecond = 1000L * 60 * 60; // 1시간 액세스 토큰 유효
    private final long refreshTokenValidMillisecond = 1000L * 60 * 60 * 24 * 30; // 30일 리프레쉬 토큰 유효
    private final long millisecondBeforeExpiry = 1000L * 60 * 60 * 24 * 7; // 7일 이내 만료인 지

    @PostConstruct
    protected void init() {
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");
    }

    public String createAccessToken(String id) {
        LOGGER.info("[createAccessToken] access 토큰 생성 시작");
        Date now = new Date();
        String accessToken = Jwts.builder()
            .setSubject(id)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + accessTokenValidMillisecond))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();

        LOGGER.info("[createAccessToken] access 토큰 생성 완료");
        return accessToken;
    }

    public String createRefreshToken(String id) {
        LOGGER.info("[createRefreshToken] 리프레시 토큰 생성 시작");
        Date now = new Date();
        String refreshToken = Jwts.builder()
                .setSubject(id)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMillisecond)) // 7일 유효
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        LOGGER.info("[createRefreshToken] 리프레시 토큰 생성 완료");
        return refreshToken;
    }


    public boolean validateToken(String token) {
        LOGGER.info("[validateToken] 토큰 유효 체크 시작");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            LOGGER.info("[validateToken] 토큰 유효 체크 완료");
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            LOGGER.info("[validateToken] 토큰 유효 체크 예외 발생");
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        LOGGER.info("[getAuthentication] 인증 정보 조회 완료, UserDetails UserName : {}",
            userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "",
            userDetails.getAuthorities());
    }

    public String getUsername(String accessToken) {
        LOGGER.info("[getUsername] access 토큰 기반 회원 구별 정보 추출");
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody()
            .getSubject();
        LOGGER.info("[getUsername] access 토큰 기반 회원 구별 정보 추출 완료, info : {}", info);
        return info;
    }

    public String resolveToken(HttpServletRequest request) {
        LOGGER.info("[resolveToken] HTTP 헤더에서 Token 값 추출");
        return request.getHeader("Authorization");
    }

    public Boolean checkExpiry(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        Date now = new Date();
        return claims.getBody().getExpiration().before(new Date(now.getTime() + millisecondBeforeExpiry));
    }
}