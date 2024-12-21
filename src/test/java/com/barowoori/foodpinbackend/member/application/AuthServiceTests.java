package com.barowoori.foodpinbackend.member.application;

import com.barowoori.foodpinbackend.member.command.application.AuthService;
import com.barowoori.foodpinbackend.member.command.application.requestDto.JoinRequest;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class AuthServiceTests {
    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("회원가입 성공 테스트")
    class JoinSuccess{
        @Test
        @Transactional
        @DisplayName("닉네임을 입력했을 때 입력한 닉네임으로 저장되어야 한다")
        void WhenExistNickname(){
            JoinRequest request = JoinRequest.builder()
                    .name("name")
                    .phone("01012341234")
                    .email("email")
                    .nickname("nickname")
                    .socialLoginType(SocialLoginType.KAKAO)
                    .socialLoginId("id123")
                    .build();
            authService.join(request);
            List<Member> members = memberRepository.findAll();
            assertEquals(1, members.size());
            assertEquals(request.getNickname(), members.stream().findFirst().get().getNickname());
        }
        @Test
        @Transactional
        @DisplayName("닉네임을 입력하지 않았을 때 랜덤으로 닉네임이 생성되어야 한다")
        void WhenNicknameIsNull(){
            JoinRequest request = JoinRequest.builder()
                    .name("name")
                    .phone("01012341234")
                    .email("email")
                    .nickname(null)
                    .socialLoginType(SocialLoginType.KAKAO)
                    .socialLoginId("id123")
                    .build();
            authService.join(request);
            List<Member> members = memberRepository.findAll();
            assertEquals(1, members.size());
            String nickname = members.stream().findFirst().get().getNickname();
            assertNotNull(nickname);
            System.out.println(nickname);
        }
    }
}
