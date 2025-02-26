package com.barowoori.foodpinbackend.member.command.application.service;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.common.security.JwtTokenProvider;
import com.barowoori.foodpinbackend.member.command.application.dto.RequestMember;
import com.barowoori.foodpinbackend.member.command.application.dto.ResponseMember;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.model.TruckLike;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.TruckLikeRepository;
import com.barowoori.foodpinbackend.member.command.domain.service.GenerateNicknameService;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.truck.command.domain.exception.TruckErrorCode;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GenerateNicknameService generateNicknameService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ImageManager imageManager;
    private final TruckLikeRepository truckLikeRepository;
    private final TruckRepository truckRepository;

    private Member getMember() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        return member;
    }

    @Transactional
    public void registerMember(RequestMember.RegisterMemberDto registerMemberDto) {

        Member member = memberRepository.findBySocialLoginInfo(registerMemberDto.getSocialInfoDto().toEntity());
        if (member != null) {
            throw new CustomException(MemberErrorCode.MEMBER_SOCIAL_LOGIN_INFO_EXISTS);
        }

        member = registerMemberDto.toEntity();
        memberRepository.save(member);
    }

    @Transactional
    public ResponseMember.LoginMemberRsDto loginMember(RequestMember.LoginMemberRqDto loginMemberRqDto) {
        Member member = memberRepository.findBySocialLoginInfo(loginMemberRqDto.getSocialInfoDto().toEntity());
        if (member == null)
            throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);
        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);
        return ResponseMember.LoginMemberRsDto.toDto(accessToken, refreshToken);
    }

    @Transactional
    public ResponseMember.ReissueTokenDto reissueToken(String refreshToken) {
        Member member = getMember();
        String accessToken;
        if (member.matchRefreshToken(refreshToken)) {
            accessToken = jwtTokenProvider.createAccessToken(member.getId());
            if (jwtTokenProvider.checkExpiry(refreshToken)) {
                refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
                member.updateRefreshToken(refreshToken);
                memberRepository.save(member);
            }
            return ResponseMember.ReissueTokenDto.toDto(accessToken, refreshToken);
        } else
            throw new CustomException(MemberErrorCode.REFRESH_TOKEN_MATCH_FAILED);
    }

    @Transactional
    public ResponseMember.GetInfoDto getInfo() {
        Member member = getMember();
        return ResponseMember.GetInfoDto.toDto(member);
    }

    @Transactional
    public ResponseMember.GenerateNicknameDto generateNickname() {
        String nickname = generateNicknameService.generationNickname();
        return ResponseMember.GenerateNicknameDto.toDto(nickname);
    }

    @Transactional
    public ResponseMember.CheckNicknameDto checkNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname);
        boolean isNicknameUsable = false;
        if (member == null) {
            isNicknameUsable = true;
        }
        return ResponseMember.CheckNicknameDto.toDto(isNicknameUsable);
    }

    @Transactional
    public ResponseMember.CheckPhoneDto checkPhone(String phone) {
        Member member = memberRepository.findByPhone(phone);
        if (member != null) {
            return ResponseMember.CheckPhoneDto.toDto(member.getSocialLoginInfo());
        }
        return ResponseMember.CheckPhoneDto.toDto(new SocialLoginInfo(SocialLoginType.valueOf("NONE"), "null"));
    }

    @Transactional
    public void updateProfile(RequestMember.UpdateProfileRqDto updateProfileRqDto, MultipartFile image) {
        Member member = getMember();
        member.updateProfile(imageManager, updateProfileRqDto.getNickname(), updateProfileRqDto.getOriginImageUrl(), image);
        memberRepository.save(member);
    }

    @Transactional
    public void logoutMember(String refreshToken) {
        Member member = getMember();
        if (member.matchRefreshToken(refreshToken)) {
            member.updateRefreshToken(null);
            memberRepository.save(member);
        } else
            throw new CustomException(MemberErrorCode.REFRESH_TOKEN_MATCH_FAILED);
    }

    @Transactional
    public void deleteMember() {
        Member member = getMember();
        memberRepository.delete(member);
    }

    @Transactional
    public void likeTruck(String truckId) {
        Member member = getMember();
        Truck truck = truckRepository.findById(truckId).orElseThrow(() -> new CustomException(TruckErrorCode.NOT_FOUND_TRUCK));
        TruckLike truckLike = truckLikeRepository.findByMemberIdAndTruckId(member.getId(), truckId);
        if (truckLike == null) {
            truckLike = TruckLike.builder()
                    .truck(truck)
                    .member(member)
                    .build();
            truckLikeRepository.save(truckLike);
        } else {
            truckLikeRepository.delete(truckLike);
        }
    }
}
