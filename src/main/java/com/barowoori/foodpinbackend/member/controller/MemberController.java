package com.barowoori.foodpinbackend.member.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.common.exception.ErrorResponse;
import com.barowoori.foodpinbackend.common.security.JwtTokenProvider;
import com.barowoori.foodpinbackend.member.command.application.dto.RequestMember;
import com.barowoori.foodpinbackend.member.command.application.dto.ResponseMember;
import com.barowoori.foodpinbackend.member.command.application.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 API", description = "회원 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입")
    @PostMapping("/v1/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "이미 해당 소셜 정보로 가입한 경우[20003]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CommonResponse<String>> registerMember(@Valid @RequestBody RequestMember.RegisterMemberDto registerMemberDto) {
        memberService.registerMember(registerMemberDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("User registered successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "로그인")
    @PostMapping("/v1/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "해당 회원 정보가 없을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CommonResponse<ResponseMember.LoginMemberRsDto>> loginMember(@Valid @RequestBody RequestMember.LoginMemberRqDto loginMemberRqDto){
        ResponseMember.LoginMemberRsDto loginMemberRsDto = memberService.loginMember(loginMemberRqDto);
        CommonResponse<ResponseMember.LoginMemberRsDto> commonResponse = CommonResponse.<ResponseMember.LoginMemberRsDto>builder()
                .data(loginMemberRsDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("/v1/info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "해당 회원 정보가 없을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CommonResponse<ResponseMember.GetMemberDto>> getMember(){
        ResponseMember.GetMemberDto getMemberDto = memberService.getMember();
        CommonResponse<ResponseMember.GetMemberDto> commonResponse = CommonResponse.<ResponseMember.GetMemberDto>builder()
                .data(getMemberDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "닉네임 자동 생성")
    @GetMapping("/v1/random-nickname")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public ResponseEntity<CommonResponse<String>> generateNickname(){
        String nickname = memberService.generateNickname();
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data(nickname)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "닉네임 중복 확인")
    @GetMapping("/v1/nickname/{nickname}/valid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public ResponseEntity<CommonResponse<Boolean>> checkNickname(@Valid @PathVariable("nickname") String nickname){
        Boolean isNicknameUsable = memberService.checkNickname(nickname);
        CommonResponse<Boolean> commonResponse = CommonResponse.<Boolean>builder()
                .data(isNicknameUsable)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
