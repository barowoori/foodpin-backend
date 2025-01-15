package com.barowoori.foodpinbackend.member.command.application.service;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.common.security.JwtTokenProvider;
import com.barowoori.foodpinbackend.member.command.application.dto.CommonMember;
import com.barowoori.foodpinbackend.member.command.application.dto.RequestMember;
import com.barowoori.foodpinbackend.member.command.application.dto.ResponseMember;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.service.GenerateNicknameService;
import com.barowoori.foodpinbackend.member.command.domain.service.ImageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GenerateNicknameService generateNicknameService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ImageManager imageManager;

    @Transactional
    public void registerMember(RequestMember.RegisterMemberDto registerMemberDto) {

        Member member = memberRepository.findBySocialLoginInfo(CommonMember.SocialInfoDto.toEntity(registerMemberDto.getSocialInfoDto()));
        if(member != null){
            throw new CustomException(MemberErrorCode.MEMBER_SOCIAL_LOGIN_INFO_EXISTS);
        }

        member = RequestMember.RegisterMemberDto.toEntity(registerMemberDto);
        memberRepository.save(member);
    }

    @Transactional
    public ResponseMember.LoginMemberRsDto loginMember(RequestMember.LoginMemberRqDto loginMemberRqDto) {
        Member member = memberRepository.findBySocialLoginInfo(CommonMember.SocialInfoDto.toEntity(loginMemberRqDto.getSocialInfoDto()));
        if (member == null)
            throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);
        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);
        return ResponseMember.LoginMemberRsDto.toDto(accessToken, refreshToken);
    }

    @Transactional
    public ResponseMember.ReissueTokenDto reissueToken(String refreshToken){
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        String accessToken;
        if (member.matchRefreshToken(refreshToken)) {
            accessToken = jwtTokenProvider.createAccessToken(member.getId());
            if (jwtTokenProvider.checkExpiry(refreshToken)){
                refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
                member.updateRefreshToken(refreshToken);
                memberRepository.save(member);
            }
            return ResponseMember.ReissueTokenDto.toDto(accessToken, refreshToken);
        }
        else
            throw new CustomException(MemberErrorCode.REFRESH_TOKEN_MATCH_FAILED);
    }

    @Transactional
    public ResponseMember.GetInfoDto getInfo(){
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        return ResponseMember.GetInfoDto.toDto(member);
    }

    @Transactional
    public ResponseMember.GenerateNicknameDto generateNickname(){
        String nickname = generateNicknameService.generationNickname();
        return ResponseMember.GenerateNicknameDto.toDto(nickname);
    }

    @Transactional
    public ResponseMember.CheckNicknameDto checkNickname(String nickname){
        Member member = memberRepository.findByNickname(nickname);
        boolean isNicknameUsable = false;
        if(member == null){
            isNicknameUsable = true;
        }
        return ResponseMember.CheckNicknameDto.toDto(isNicknameUsable);
    }

    @Transactional
    public ResponseMember.CheckPhoneDto checkPhone(String phone){
        Member member = memberRepository.findByPhone(phone);
        boolean isRegistered = false;
        if (member != null) {
            isRegistered = true;
            return  ResponseMember.CheckPhoneDto.toDto(isRegistered, member.getSocialLoginInfo());
        }
        return ResponseMember.CheckPhoneDto.toDto(isRegistered, new SocialLoginInfo(SocialLoginType.valueOf("NONE"),"null"));
    }

    @Transactional
    public void updateProfile(RequestMember.UpdateProfileRqDto updateProfileRqDto, MultipartFile image){
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        member.updateProfile(imageManager, updateProfileRqDto.getNickname(), updateProfileRqDto.getOriginImageUrl(), image);
        memberRepository.save(member);
    }

    @Transactional
    public void logoutMember(String refreshToken){
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        if (member.matchRefreshToken(refreshToken)) {
            member.updateRefreshToken(null);
            memberRepository.save(member);
        } else
            throw new CustomException(MemberErrorCode.REFRESH_TOKEN_MATCH_FAILED);
    }
}
