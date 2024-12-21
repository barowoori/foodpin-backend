package com.barowoori.foodpinbackend.member.controller;

import com.barowoori.foodpinbackend.member.command.application.AuthService;
import com.barowoori.foodpinbackend.member.command.application.requestDto.JoinRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/v1/join")
    public void join(@RequestBody JoinRequest request){
        authService.join(request);
    }
}
