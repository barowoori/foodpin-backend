package com.barowoori.foodpinbackend.config.security;

import com.barowoori.foodpinbackend.common.security.CustomAccessDeniedHandler;
import com.barowoori.foodpinbackend.common.security.CustomAuthenticationEntryPoint;
import com.barowoori.foodpinbackend.common.security.JwtAuthenticationFilter;
import com.barowoori.foodpinbackend.common.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST API는 csrf 보안이 필요 없으므로 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // JWT Token 인증방식으로 세션은 필요 없으므로 비활성화
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //리퀘스트에 대한 사용 권한 체크
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**",
                                "/api/members/v1/register", "/api/members/v1/login", "/api/members/v1/random-nickname"
                                ,"/api/members/v1/nickname/{nickname}/valid", "/api/members/v1/phone/{phone}/valid","/api/files/**","/api/documents/**").permitAll()
                        .requestMatchers("**exception**").permitAll())

                // 나머지 요청은 인증된 NORMAL 접근 가능
                .authorizeHttpRequests(authorize-> authorize.anyRequest().hasRole("NORMAL"))
                //우리 서비스에 대한 권한은 있지만 다른 권한일 경우
                .exceptionHandling(handler -> handler.accessDeniedHandler(new CustomAccessDeniedHandler()))
                //우리 서비스에 권한 자체가 없을 경우
                .exceptionHandling(handler -> handler.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                //토큰 유효 체크 필터 -> 아이디/비번 체크 필터 순으로 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
