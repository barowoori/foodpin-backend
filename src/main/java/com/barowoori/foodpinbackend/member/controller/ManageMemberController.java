package com.barowoori.foodpinbackend.member.controller;

import com.barowoori.foodpinbackend.member.command.application.ManageMemberService;
import com.barowoori.foodpinbackend.member.command.application.requestDto.ProfileRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class ManageMemberController {
    private ManageMemberService memberService;

    public ManageMemberController(ManageMemberService memberService) {
        this.memberService = memberService;
    }

    @PutMapping("/v1/profile")
    public void updateProfile(@RequestBody ProfileRequest request){
        String memberId = "";
        memberService.updateProfile(memberId, request);
    }
}
