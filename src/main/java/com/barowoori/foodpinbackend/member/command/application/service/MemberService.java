package com.barowoori.foodpinbackend.member.command.application.service;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.common.security.JwtTokenProvider;
import com.barowoori.foodpinbackend.member.command.application.dto.RequestMember;
import com.barowoori.foodpinbackend.member.command.application.dto.ResponseMember;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.service.GenerateNicknameService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GenerateNicknameService generateNicknameService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void registerMember(RequestMember.RegisterMemberDto registerMemberDto) {

        Member member = memberRepository.findBySocialLoginInfo(registerMemberDto.getSocialLoginInfo());
        if(member != null){
            throw new CustomException(MemberErrorCode.MEMBER_SOCIAL_LOGIN_INFO_EXISTS);
        }

        member = RequestMember.RegisterMemberDto.toEntity(registerMemberDto);
        memberRepository.save(member);
    }

    @Transactional
    public ResponseMember.LoginMemberRsDto loginMember(RequestMember.LoginMemberRqDto loginMemberRqDto) {
        Member member = memberRepository.findBySocialLoginInfo(loginMemberRqDto.getSocialLoginInfo());
        if (member == null)
            throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);
        String accessToken = jwtTokenProvider.createToken(member.getId(), member.getTypes());
        return ResponseMember.LoginMemberRsDto.toDto(accessToken);
    }

    @Transactional
    public ResponseMember.GetMemberDto getMember(){
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        return ResponseMember.GetMemberDto.toDto(member);
    }

    @Transactional
    public String generateNickname(){
        return generateNicknameService.generationNickname();
    }

    @Transactional
    public Boolean checkNickname(String nickname){
        Member member = memberRepository.findByNickname(nickname);
        boolean isNicknameUsable = false;
        if(member == null){
            isNicknameUsable = true;
        }
        return isNicknameUsable;
    }
}
