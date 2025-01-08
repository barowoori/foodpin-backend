package com.barowoori.foodpinbackend.member.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.common.security.JwtTokenProvider;
import com.barowoori.foodpinbackend.member.command.application.dto.RequestMember;
import com.barowoori.foodpinbackend.member.command.application.dto.ResponseMember;
import com.barowoori.foodpinbackend.member.command.application.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/member")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse> registerMember(@Valid @RequestBody RequestMember.RegisterMemberDto registerMemberDto) {
        memberService.registerMember(registerMemberDto);
        CommonResponse commonResponse = CommonResponse.builder()
                .data("User registered successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse> loginMember(@Valid @RequestBody RequestMember.LoginMemberRqDto loginMemberRqDto){
        ResponseMember.LoginMemberRsDto loginMemberRsDto = memberService.loginMember(loginMemberRqDto);
        CommonResponse commonResponse = CommonResponse.builder()
                .data(loginMemberRsDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @GetMapping("/get")
    public ResponseEntity<CommonResponse> getMember(){
        ResponseMember.GetMemberDto getMemberDto = memberService.getMember();
        CommonResponse commonResponse = CommonResponse.builder()
                .data(getMemberDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @GetMapping("/generateNickname")
    public ResponseEntity<CommonResponse> generateNickname(){
        String nickname = memberService.generateNickname();
        CommonResponse commonResponse = CommonResponse.builder()
                .data(nickname)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @GetMapping("/checkNickname/{nickname}")
    public ResponseEntity<CommonResponse> checkNickname(@Valid @PathVariable("nickname") String nickname){
        Boolean isNicknameUsable = memberService.checkNickname(nickname);
        CommonResponse commonResponse = CommonResponse.builder()
                .data(isNicknameUsable)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
