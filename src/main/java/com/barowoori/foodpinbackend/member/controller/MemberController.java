package com.barowoori.foodpinbackend.member.controller;

import com.barowoori.foodpinbackend.common.dto.CommonResponse;
import com.barowoori.foodpinbackend.common.exception.ErrorResponse;
import com.barowoori.foodpinbackend.common.security.JwtTokenProvider;
import com.barowoori.foodpinbackend.member.command.application.dto.RequestMember;
import com.barowoori.foodpinbackend.member.command.application.dto.ResponseMember;
import com.barowoori.foodpinbackend.member.command.application.service.MemberService;
import com.barowoori.foodpinbackend.region.command.domain.model.Region;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionDo;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionSi;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Tag(name = "회원 API", description = "회원 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "이미 해당 소셜 정보로 가입한 경우[20003]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/v1/register")
    public ResponseEntity<CommonResponse<String>> registerMember(@Valid @RequestBody RequestMember.RegisterMemberDto registerMemberDto) {
        memberService.registerMember(registerMemberDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Member registered successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "임시 회원가입", description = "SocialLoginType은 UNREGISTERED 필수")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "이미 해당 소셜 정보로 가입한 경우[20003]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "SocialLoginType이 UNREGISTERED가 아닌 경우[20007]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/v1/register/temporary")
    public ResponseEntity<CommonResponse<String>> registerTemporary(@Valid @RequestBody RequestMember.RegisterTemporaryDto registerTemporaryDto) {
        memberService.registerTemporary(registerTemporaryDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Temporary registered successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "로그인", description = "반환되는 accessToken, refreshToken 전부 저장 후" +
            "\n\n모든 요청의 Authorization 헤더에 accessToken을 담아서 사용(/reissued-token, /logout API는 refreshToken)" +
            "\n\naccessToken(유효기간 1시간) 만료(401 에러) 시 /reissued-token API로 액세스 토큰 재발급" +
            "\n\nrefreshToken(유효기간 30일)은 만료(401 에러) 시 /login API로 액세스, 리프레쉬 전부 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "해당 회원 정보가 없을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/v1/login")
    public ResponseEntity<CommonResponse<ResponseMember.LoginMemberRsDto>> loginMember(@Valid @RequestBody RequestMember.LoginMemberRqDto loginMemberRqDto) {
        ResponseMember.LoginMemberRsDto loginMemberRsDto = memberService.loginMember(loginMemberRqDto);
        CommonResponse<ResponseMember.LoginMemberRsDto> commonResponse = CommonResponse.<ResponseMember.LoginMemberRsDto>builder()
                .data(loginMemberRsDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "액세스 토큰 재발급", description = "Authorization 헤더에 RefreshToken 입력" +
            "\n\n해당 API 호출 시 리프레쉬 토큰이 만료 7일 전부터 자동 갱신되므로 반환되는 액세스, 리프레쉬 토큰 전부 저장 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(리프레쉬 토큰 만료), 리프레시 토큰이 일치하지 않는 경우[20005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 회원 정보가 없을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/v1/reissued-token")
    public ResponseEntity<CommonResponse<ResponseMember.ReissueTokenDto>> reissueToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);
        ResponseMember.ReissueTokenDto reissueTokenDto = memberService.reissueToken(refreshToken);
        CommonResponse<ResponseMember.ReissueTokenDto> commonResponse = CommonResponse.<ResponseMember.ReissueTokenDto>builder()
                .data(reissueTokenDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "회원 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 회원 정보가 없을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/v1/info")
    public ResponseEntity<CommonResponse<ResponseMember.GetInfoDto>> getInfo() {
        ResponseMember.GetInfoDto getInfoDto = memberService.getInfo();
        CommonResponse<ResponseMember.GetInfoDto> commonResponse = CommonResponse.<ResponseMember.GetInfoDto>builder()
                .data(getInfoDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "닉네임 자동 생성", description = "토큰 불필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/v1/random-nickname")
    public ResponseEntity<CommonResponse<ResponseMember.GenerateNicknameDto>> generateNickname() {
        ResponseMember.GenerateNicknameDto generateNicknameDto = memberService.generateNickname();
        CommonResponse<ResponseMember.GenerateNicknameDto> commonResponse = CommonResponse.<ResponseMember.GenerateNicknameDto>builder()
                .data(generateNicknameDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "닉네임 중복 확인", description = "토큰 불필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/v1/nickname/{nickname}/valid")
    public ResponseEntity<CommonResponse<ResponseMember.CheckNicknameDto>> checkNickname(@Valid @PathVariable("nickname") String nickname) {
        ResponseMember.CheckNicknameDto checkNicknameDto = memberService.checkNickname(nickname);
        CommonResponse<ResponseMember.CheckNicknameDto> commonResponse = CommonResponse.<ResponseMember.CheckNicknameDto>builder()
                .data(checkNicknameDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "번호 가입 여부 확인", description = "토큰 불필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/v1/phone/{phone}/valid")
    public ResponseEntity<CommonResponse<ResponseMember.CheckPhoneDto>> checkPhone(@Valid @PathVariable("phone") String phone) {
        ResponseMember.CheckPhoneDto checkPhoneDto = memberService.checkPhone(phone);
        CommonResponse<ResponseMember.CheckPhoneDto> commonResponse = CommonResponse.<ResponseMember.CheckPhoneDto>builder()
                .data(checkPhoneDto)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "번호 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "번호가 비어있을 경우[20001]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 회원 정보가 없을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/phone/{phone}")
    public ResponseEntity<CommonResponse<String>> updatePhone(@Valid @PathVariable("phone") String phone) {
        memberService.updatePhone(phone);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Phone updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "프로필(사진, 닉네임) 수정", description = "닉네임은 필수 입력" +
            "\n\n기존 이미지 경로, 새 이미지 파일 둘 다 입력 없는 경우 프로필 이미지 삭제" +
            "\n\n기존 이미지 경로 입력 유무와는 상관없이 새 이미지 파일이 들어오는 경우 새 파일로 프로필 이미지 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "닉네임이 비어있을 경우[20000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(액세스 토큰 만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 회원 정보가 없을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/v1/profile")
    public ResponseEntity<CommonResponse<String>> updateProfile(@Valid @RequestBody RequestMember.UpdateProfileRqDto updateProfileRqDto) {
        memberService.updateProfile(updateProfileRqDto);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Profile updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "로그아웃", description = "Authorization 헤더에 RefreshToken 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(리프레쉬 토큰 만료), 리프레시 토큰이 일치하지 않는 경우[20005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 회원 정보가 없을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/v1/logout")
    public ResponseEntity<CommonResponse<String>> logoutMember(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);
        memberService.logoutMember(refreshToken);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Member logged out successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }


    @Operation(summary = "임시 회원삭제", description = "Authorization 헤더에 RefreshToken 입력, 임시 회원 삭제 기능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "권한이 없을 경우(리프레쉬 토큰 만료), 리프레시 토큰이 일치하지 않는 경우[20005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 회원 정보가 없을 경우[20004]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/v1/test")
    public ResponseEntity<CommonResponse<String>> deleteMember(HttpServletRequest request) {
        memberService.deleteMember();
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Member deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "푸드트럭 좋아요/취소", description = "좋아요가 이미 있을 경우 좋아요 취소, 없을 경우 좋아요 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "해당 회원 정보가 없을 경우[20004], 해당 트럭 정보가 없을 경우[30000]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/v1/trucks/{truckId}/like")
    public ResponseEntity<CommonResponse<String>> likeTruck(@Valid @PathVariable(name = "truckId") String truckId) {
        memberService.likeTruck(truckId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Truck liked successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(summary = "행사 좋아요/취소", description = "좋아요가 이미 있을 경우 좋아요 취소, 없을 경우 좋아요 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "해당 회원 정보가 없을 경우[20004], 해당 행사 정보가 없을 경우[40005]",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/v1/events/{eventId}/like")
    public ResponseEntity<CommonResponse<String>> likeEvent(@Valid @PathVariable(name = "eventId") String eventId) {
        memberService.likeEvent(eventId);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Event liked successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }
}
