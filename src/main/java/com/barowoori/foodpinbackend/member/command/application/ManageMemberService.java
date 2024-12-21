package com.barowoori.foodpinbackend.member.command.application;

import com.barowoori.foodpinbackend.member.command.application.requestDto.ProfileRequest;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.service.ImageManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManageMemberService {
    private final MemberRepository memberRepository;
    private final ImageManager imageManager;

    public ManageMemberService(MemberRepository memberRepository, ImageManager imageManager) {
        this.memberRepository = memberRepository;
        this.imageManager = imageManager;
    }

    @Transactional
    public void updateProfile(String memberId, ProfileRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("NOT FOUND MEMBER"));
        member.updateProfile(imageManager, request.getNickname(), request.getImage());
    }
}
