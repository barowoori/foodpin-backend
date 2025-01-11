package com.barowoori.foodpinbackend.truck.domain.repository;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManager;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManagerRole;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckManagerRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckManagerSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TruckManagerRepositoryTests {
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TruckManagerRepository truckManagerRepository;

    Truck truck;
    Member owner;
    Member member;
    TruckManager truckOwner;
    TruckManager truckMember;

    @BeforeEach
    void setUp() {
        truck = Truck.builder()
                .name("바로우리")
                .description("바로우리 트럭입니다")
                .isDeleted(Boolean.FALSE)
                .build();
        truck = truckRepository.save(truck);

        owner = createMember("1");
        member = createMember("2");

        truckOwner = createTruckManager(owner, TruckManagerRole.OWNER);

        truckMember = createTruckManager(member, TruckManagerRole.MEMBER);
    }

    private TruckManager createTruckManager(Member member, TruckManagerRole role) {
        TruckManager truckManager = TruckManager.builder()
                .truck(truck)
                .member(member)
                .role(TruckManagerRole.MEMBER)
                .build();
        return truckManagerRepository.save(truckManager);

    }

    private Member createMember(String number) {
        Member member = Member.builder()
                .email("email")
                .phone(number)
                .nickname(number)
                .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, number))
                .build();
        return memberRepository.save(member);
    }

    @Nested
    @DisplayName("트럭 운영자 목록 테스트")
    class TruckManagerPage {
        @Test
        @Transactional
        @DisplayName("트럭 운영자 목록은 본인이 제일 상단에 조회되어야 한다(소유자일 경우)")
        void WhenOwner() {
            Page<TruckManagerSummary> truckManagerSummaryPage = truckManagerRepository.findTruckManagerPages(truck.getId(), owner.getId(), PageRequest.of(0, 10));
            assertEquals(2, truckManagerSummaryPage.getTotalElements());
            assertEquals(truckOwner.getId(), truckManagerSummaryPage.get().findFirst().get().getTruckManagerId());
        }

        @Test
        @Transactional
        @DisplayName("트럭 운영자 목록은 본인이 제일 상단에 조회되어야 한다(운영자일 경우)")
        void WhenManager() {
            Page<TruckManagerSummary> truckManagerSummaryPage = truckManagerRepository.findTruckManagerPages(truck.getId(), member.getId(), PageRequest.of(0, 10));
            assertEquals(2, truckManagerSummaryPage.getTotalElements());
            assertEquals(truckMember.getId(), truckManagerSummaryPage.get().findFirst().get().getTruckManagerId());
        }

        @Test
        @Transactional
        @DisplayName("두번째 페이지에선 본인이 상단에 위치하지 않는다")
        void WhenNextPageIsNotStartMe() {
            for (int i = 3; i <= 20; i++) {
                createTruckManager(createMember(String.valueOf(i)), TruckManagerRole.MEMBER);
            }
            Page<TruckManagerSummary> truckManagerSummaryPage = truckManagerRepository.findTruckManagerPages(truck.getId(), member.getId(), PageRequest.of(1, 10));
            assertEquals(20, truckManagerSummaryPage.getTotalElements());
            assertEquals(10, truckManagerSummaryPage.getNumberOfElements());
            assertNotEquals(truckMember.getId(), truckManagerSummaryPage.get().findFirst().get().getTruckManagerId());
        }

        @Test
        @Transactional
        @DisplayName("트럭 운영자 목록은 본인이 제일 상단에 조회되고, 그 뒤로는 등록일 기준 오름차순으로 정렬되어야 한다.(닉네임을 1씩 증가하여 지정할 때 이전 닉네임이 다음 닉네임보다 작아야 한다)")
        void OrderByCreateAtAsc() {
            for (int i = 3; i <= 20; i++) {
                createTruckManager(createMember(String.valueOf(i)), TruckManagerRole.MEMBER);
            }
            Page<TruckManagerSummary> truckManagerSummaryPage = truckManagerRepository.findTruckManagerPages(truck.getId(), member.getId(), PageRequest.of(0, 10));
            assertEquals(20, truckManagerSummaryPage.getTotalElements());
            assertEquals(truckMember.getId(), truckManagerSummaryPage.get().findFirst().get().getTruckManagerId());
            List<TruckManagerSummary> summaries = truckManagerSummaryPage.getContent();
            for (int i = 1; i < summaries.size() - 1; i++) {
                assertTrue(Integer.valueOf(summaries.get(i).getNickname()) < Integer.valueOf(summaries.get(i + 1).getNickname()));
            }
        }
    }


}
