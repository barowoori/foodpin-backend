package com.barowoori.foodpinbackend.member.domain;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.MemberType;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.service.GenerateNicknameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class MemberTests {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GenerateNicknameService generateNicknameService;

    @Nested
    @DisplayName("회원 생성 성공 테스트")
    class CreateMemberSuccess{
        @Test
        @Transactional
        @DisplayName("닉네임을 입력했을 때 입력한 닉네임으로 저장되어야 한다")
        void WhenExistNickname(){
            Member memberBuilder = Member.builder()
                    .nicknameGenerator(generateNicknameService)
                    .email("email")
                    .phone("01012341234")
                    .nickname("nickname")
                    .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                    .build();
           Member member = memberRepository.save(memberBuilder);
           assertEquals(memberBuilder.getNickname(), member.getNickname());
        }
        @Test
        @Transactional
        @DisplayName("닉네임을 입력하지 않았을 때 랜덤으로 닉네임이 생성되어야 한다")
        void WhenNicknameIsNull(){
            Member memberBuilder = Member.builder()
                    .nicknameGenerator(generateNicknameService)
                    .email("email")
                    .phone("01012341234")
                    .nickname(null)
                    .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                    .build();
            Member member = memberRepository.save(memberBuilder);
            assertNotNull(member.getNickname());
            System.out.println(member.getNickname());
        }

        @Test
        @Transactional
        @DisplayName("회원 유형은 NORMAL가 기본값으로 설정되어 있다")
        void MemberType_Default_NORMAL(){
            Member memberBuilder = Member.builder()
                    .nicknameGenerator(generateNicknameService)
                    .email("email")
                    .phone("01012341234")
                    .nickname("nickname")
                    .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                    .build();
            Member member = memberRepository.save(memberBuilder);
            assertEquals(MemberType.NORMAL, member.getType());
        }
    }

    @Nested
    @DisplayName("회원 생성 실패 테스트")
    class CreateMemberFail{
        @Test
        @Transactional
        @DisplayName("핸드폰 번호가 없으면 예외 처리")
        void WhenPhoneIsNull(){
            assertThrows(IllegalArgumentException.class, () -> {
                Member.builder()
                        .nicknameGenerator(generateNicknameService)
                        .email("email")
                        .phone(null)
                        .nickname(null)
                        .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                        .build();
            });
        }
    }

}
