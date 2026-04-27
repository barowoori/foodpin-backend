package com.barowoori.foodpinbackend.member.controller;

import com.barowoori.foodpinbackend.member.command.application.service.AppleAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth/apple")
public class AppleAuthController {
    private final AppleAuthService appleAuthService;

    public AppleAuthController(AppleAuthService appleAuthService) {
        this.appleAuthService = appleAuthService;
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> appleCallback(
            @RequestParam("code") String authorizationCode,
            @RequestParam("id_token") String idToken,
            @RequestParam(value = "state", required = false) String state
    ) {
        String redirectUrl = appleAuthService.makeCallBackRedirectURL(authorizationCode, idToken, state);

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER) // 303 추천
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }
}