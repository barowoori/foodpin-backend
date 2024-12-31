package com.barowoori.foodpinbackend.member.domain;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.member.command.domain.exception.MemberErrorCode;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.MemberType;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.service.GenerateNicknameService;
import com.barowoori.foodpinbackend.member.command.domain.service.ImageManager;
import com.barowoori.foodpinbackend.member.infra.domain.ImageDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class MemberTests {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GenerateNicknameService generateNicknameService;

    @Mock
    private ImageManager imageManager;

    @Mock
    private MultipartFile multipartFile;

    @Nested
    @DisplayName("회원 생성 성공 테스트")
    class CreateMemberSuccess {
        @Test
        @Transactional
        @DisplayName("닉네임을 입력했을 때 입력한 닉네임으로 저장되어야 한다")
        void WhenExistNickname() {
            Member memberBuilder = Member.builder()
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
        @DisplayName("회원 유형은 NORMAL가 기본값으로 설정되어 있다")
        void MemberType_Default_NORMAL() {
            Member memberBuilder = Member.builder()
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
    class CreateMemberFail {
        @Test
        @Transactional
        @DisplayName("핸드폰 번호가 없으면 예외 처리")
        void WhenPhoneIsNull() {
            CustomException exception = assertThrows(CustomException.class, () -> {
                Member.builder()
                        .email("email")
                        .phone(null)
                        .nickname("nickname")
                        .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                        .build();
            });
            assertEquals(MemberErrorCode.MEMBER_PHONE_EMPTY.getMessage(), exception.getMessage());
        }

        @Test
        @Transactional
        @DisplayName("닉네임이 없으면 예외 처리")
        void WhenNicknameIsNull() {
            CustomException exception = assertThrows(CustomException.class, () -> {
                Member.builder()
                        .email("email")
                        .phone("01012341234")
                        .nickname(null)
                        .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                        .build();
            });
            assertEquals(MemberErrorCode.MEMBER_NICKNAME_EMPTY.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("회원 프로필 변경 테스트")
    class UpdateProfileSuccess {
        private Member member;

        @BeforeEach
        void setUp() throws IOException {
            MockitoAnnotations.openMocks(this);

            String fileName = "test-file.jpg";
            byte[] fileContent = "test-content".getBytes();
            InputStream fileInputStream = new ByteArrayInputStream(fileContent);

            when(multipartFile.getOriginalFilename()).thenReturn(fileName);
            when(multipartFile.getBytes()).thenReturn(fileContent);
            when(multipartFile.getInputStream()).thenReturn(fileInputStream);

            Member memberBuilder = Member.builder()
                    .email("email")
                    .phone("01012341234")
                    .nickname("nickname")
                    .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                    .image("originImageUrl")
                    .build();
            member = memberRepository.save(memberBuilder);

            when(imageManager.updateFile(multipartFile, "originImageUrl", ImageDirectory.PROFILE))
                    .thenReturn("updatedImageUrl");

        }

        @Test
        @Transactional
        @DisplayName("닉네임은 전달된 값으로 변경된다")
        void WhenChangedNickname() {
            String originNickname = member.getNickname();
            assertEquals("nickname", originNickname);

            member.updateProfile(imageManager, member.getNickname() + "1", "originImageUrl", null);

            String updatedNickname = member.getNickname();
            assertEquals(originNickname + "1", updatedNickname);
        }

        @Test
        @Transactional
        @DisplayName("새 파일이 없고 기존 이미지 url을 전달할 경우 이미지는 변경되지 않는다")
        void WhenNotExistNewFileAndExistOriginImageUrl() {
            String originImage = member.getImage();
            assertEquals("originImageUrl", originImage);

            member.updateProfile(imageManager, member.getNickname(), "originImageUrl", null);

            String updatedImage = member.getImage();
            assertEquals(originImage, updatedImage);
        }

        @Test
        @Transactional
        @DisplayName("새 파일이 있으면 이미지는 변경된다")
        void WhenExistNewFileAndNotExistOriginImageUrl() {
            String originImage = member.getImage();
            assertEquals("originImageUrl", originImage);

            member.updateProfile(imageManager, member.getNickname(), null, multipartFile);

            String updatedImage = member.getImage();
            assertEquals("updatedImageUrl", updatedImage);
            assertNotEquals(originImage, updatedImage);
        }

        @Test
        @Transactional
        @DisplayName("새 파일이 있으면 기존 이미지 url을 보내도 이미지는 변경된다")
        void WhenExistNewFileAndExistOriginImageUrl() {
            String originImage = member.getImage();
            assertEquals("originImageUrl", originImage);

            member.updateProfile(imageManager, member.getNickname(), "originImageUrl", multipartFile);

            String updatedImage = member.getImage();
            assertEquals("updatedImageUrl", updatedImage);
            assertNotEquals(originImage, updatedImage);
        }

        @Test
        @Transactional
        @DisplayName("새 파일과 기존 이미지 url이 없을 경우 이미지는 null로 저장된다")
        void WhenNotExistNewFileAndNotExistOriginImageUrl() {
            String originImage = member.getImage();
            assertEquals("originImageUrl", originImage);

            member.updateProfile(imageManager, member.getNickname(), null, null);

            assertNull(member.getImage());
        }
    }

    @Nested
    @DisplayName("회원 핸드폰 번호 변경 테스트")
    class UpdatePhone {
        private Member member;

        @BeforeEach
        void setUp() {
            Member memberBuilder = Member.builder()
                    .email("email")
                    .phone("01012341234")
                    .nickname("nickname")
                    .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                    .image("originImageUrl")
                    .build();
            member = memberRepository.save(memberBuilder);
        }

        @Test
        @Transactional
        @DisplayName("전달 받은 값으로 핸드폰 번호가 변경된다")
        void WhenChangePhoneSuccess() {
            String originPhone = member.getPhone();
            assertEquals("01012341234", originPhone);

            member.updatePhone(member.getPhone() + "1");

            String updatedPhone = member.getPhone();
            assertEquals(originPhone + "1", updatedPhone);
        }

        @Test
        @Transactional
        @DisplayName("핸드폰 번호는 null이 될 수 없다")
        void WhenChangePhoneFailed() {
            CustomException exception = assertThrows(CustomException.class, () -> member.updatePhone(null));
            assertEquals(MemberErrorCode.MEMBER_PHONE_EMPTY.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("리프래시 토큰 관리 테스트")
    class ManageRefreshToken {

        private Member member;

        @BeforeEach
        void setUp() {
            Member memberBuilder = Member.builder()
                    .email("email")
                    .phone("01012341234")
                    .nickname("nickname")
                    .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                    .image("originImageUrl")
                    .refreshToken("originToken")
                    .build();
            member = memberRepository.save(memberBuilder);
        }

        @Test
        @Transactional
        @DisplayName("업데이트할 때 리프레시 토큰은 전달받은 값으로 변경된다")
        void WhenChangeRefreshTokenSuccess() {
            member.updateRefreshToken("updatedToken");
            assertEquals("updatedToken", member.getRefreshToken());
        }


        @Test
        @Transactional
        @DisplayName("리프레시 토큰이 일치하면 true를 반환한다")
        void WhenMatchRefreshTokenSuccess() {
            assertTrue(member.matchRefreshToken(member.getRefreshToken()));
        }

        @Test
        @Transactional
        @DisplayName("리프레시 토큰이 일치하지 않으면 false를 반환한다")
        void WhenNotMatchRefreshTokenSuccess() {
            assertFalse(member.matchRefreshToken(member.getRefreshToken() + "1"));
        }

        @Test
        @Transactional
        @DisplayName("기존 리프레시 토큰이 null이면 예외 처리")
        void WhenOriginRefreshTokenIsNull() {
            member.updateRefreshToken(null);

            CustomException exception = assertThrows(CustomException.class, () -> member.matchRefreshToken("new"));
            assertEquals(MemberErrorCode.MEMBER_ORIGIN_REFRESH_TOKEN_EMPTY.getMessage(), exception.getMessage());
        }
    }
}

