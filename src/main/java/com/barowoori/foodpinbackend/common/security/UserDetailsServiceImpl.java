package com.barowoori.foodpinbackend.common.security;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.GuestMember;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.UnregisteredMemberRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Primary
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final UnregisteredMemberRedisRepository unregisteredMemberRedisRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        if (username.startsWith("GUEST:")) {
            String sessionId = username.substring("GUEST:".length());
            var session = unregisteredMemberRedisRepository.findBySessionId(sessionId);
            if (session == null) {
                throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);
            }
            return new GuestMember(session.getSessionId(), session.getSocialLoginId());
        }

        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        return member;
    }
}
