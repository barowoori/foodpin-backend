package com.barowoori.foodpinbackend.member.command.application;

import com.barowoori.foodpinbackend.member.command.application.requestDto.JoinRequest;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.service.GenerateNicknameService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final GenerateNicknameService nicknameGenerator;

    public AuthService(MemberRepository memberRepository, GenerateNicknameService nicknameGenerator){
        this.memberRepository = memberRepository;
        this.nicknameGenerator = nicknameGenerator;
    }

    @Transactional
    public void join(JoinRequest joinRequest){
        Member member = Member.builder()
                .nicknameGenerator(nicknameGenerator)
                .name(joinRequest.getName())
                .email(joinRequest.getEmail())
                .phone(joinRequest.getPhone())
                .nickname(joinRequest.getNickname())
                .socialLoginInfo(new SocialLoginInfo(joinRequest.getSocialLoginType(), joinRequest.getSocialLoginId()))
                .build();
        memberRepository.save(member);
    }

}
