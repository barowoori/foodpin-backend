package com.barowoori.foodpinbackend.member.controller;

import com.barowoori.foodpinbackend.member.command.application.service.AppleAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/apple")
public class AppleAuthController {
    private final AppleAuthService appleAuthService;

    public AppleAuthController(AppleAuthService appleAuthService) {
        this.appleAuthService = appleAuthService;
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> appleCallback(@RequestParam("code") String authorizationCode, @RequestParam("id_token") String idToken) {
        String redirectUrl = appleAuthService.makeCallBackRedirectURL(authorizationCode, idToken);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }
}