package com.barowoori.foodpinbackend.config.security;

import com.barowoori.foodpinbackend.common.security.CustomAccessDeniedHandler;
import com.barowoori.foodpinbackend.common.security.CustomAuthenticationEntryPoint;
import com.barowoori.foodpinbackend.common.security.JwtAuthenticationFilter;
import com.barowoori.foodpinbackend.common.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final Environment environment;
    @Value("${swagger.basic.username:}")
    private String swaggerBasicUsername;
    @Value("${swagger.basic.password:}")
    private String swaggerBasicPassword;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, Environment environment) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.environment = environment;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http) throws Exception {
        boolean isProdProfile = List.of(environment.getActiveProfiles()).contains("prod");

        http
                .securityMatcher("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**", "/index.html")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        if (!isProdProfile) {
            http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
            return http.build();
        }

        if (!isSwaggerBasicAuthConfigured()) {
            http.authorizeHttpRequests(authorize -> authorize.anyRequest().denyAll());
            return http.build();
        }

        http
                .authenticationProvider(swaggerAuthenticationProvider())
                .authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("SWAGGER"))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // REST API는 csrf 보안이 필요 없으므로 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // JWT Token 인증방식으로 세션은 필요 없으므로 비활성화
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //리퀘스트에 대한 사용 권한 체크
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/members/v1/register", "/api/members/v1/register/temporary", "/api/members/v2/login/temporary", "/api/members/v2/login", "/api/members/v1/random-nickname", "/api/members/v2/login/backoffice"
                                , "/api/members/v1/nickname/{nickname}/valid", "/api/members/v1/phone/{phone}/valid", "/api/files/**", "/api/documents/**", "/api/auth/apple/callback").permitAll()
                        .requestMatchers("/api/trucks/v1", "/api/trucks/v1/avg-menu-price/max", "/api/events/v1",
                                "/api/events/progress/status/{status}", "/api/trucks/v1/completed/status/{status}", "/api/trucks/v1/{truckId}/contact", "/api/events/v1/{eventId}/contact").hasAnyRole("NORMAL", "UNREGISTERED")
                        .requestMatchers("**exception**", "/share/**", "/api/trucks/v1/{truckId}/detail", "/api/events/v1/{eventId}/detail").permitAll())

                // 나머지 요청은 인증된 NORMAL 접근 가능
                .authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("NORMAL"))
                //우리 서비스에 대한 권한은 있지만 다른 권한일 경우
                .exceptionHandling(handler -> handler.accessDeniedHandler(new CustomAccessDeniedHandler()))
                //우리 서비스에 권한 자체가 없을 경우
                .exceptionHandling(handler -> handler.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                //토큰 유효 체크 필터 -> 아이디/비번 체크 필터 순으로 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "https://foodpin-web.pages.dev",
                "https://dev.barowoori.click",
                "https://barowoori.click",
                "http://localhost:5173",
                "https://foodpin-admin-fe.vercel.app",
                "https://www.barowoori.click",
                "https://appleid.apple.com"
        ));
        config.setAllowCredentials(true);

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder swaggerPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService swaggerUserDetailsService() {
        if (!isSwaggerBasicAuthConfigured()) {
            return new InMemoryUserDetailsManager();
        }

        return new InMemoryUserDetailsManager(
                User.builder()
                        .username(swaggerBasicUsername)
                        .password(swaggerPasswordEncoder().encode(swaggerBasicPassword))
                        .roles("SWAGGER")
                        .build()
        );
    }

    @Bean
    public AuthenticationProvider swaggerAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(swaggerUserDetailsService());
        provider.setPasswordEncoder(swaggerPasswordEncoder());
        return provider;
    }

    private boolean isSwaggerBasicAuthConfigured() {
        return swaggerBasicUsername != null && !swaggerBasicUsername.isBlank()
                && swaggerBasicPassword != null && !swaggerBasicPassword.isBlank();
    }
}
