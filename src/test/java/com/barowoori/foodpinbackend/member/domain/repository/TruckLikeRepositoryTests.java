package com.barowoori.foodpinbackend.member.domain.repository;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.model.TruckLike;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.TruckLikeRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
public class TruckLikeRepositoryTests {
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TruckLikeRepository truckLikeRepository;

    Truck truck;
    Member member;

    @BeforeEach
    void setUp() {
        truck = Truck.builder()
                .name("바로우리")
                .description("바로우리 트럭입니다")
                .isDeleted(Boolean.FALSE)
                .build();
        truck = truckRepository.save(truck);

        member = Member.builder()
                .email("email")
                .phone("01012341234")
                .nickname("nickname")
                .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                .build();
        member = memberRepository.save(member);
    }

    @Test
    @DisplayName("트럭에 좋아요를 눌렀을 때는 엔티티가 조회되어야 한다")
    void When_truckLikes() {
        TruckLike truckLike = TruckLike.builder()
                .truck(truck)
                .member(member)
                .build();
        truckLike = truckLikeRepository.save(truckLike);
        assertNotNull(truckLikeRepository.findByMemberIdAndTruckId(member.getId(), truck.getId()));
    }

    @Test
    @DisplayName("트럭에 좋아요를 누르지 않았을 때는 엔티티가 조회되지 않는다")
    void When_truckNotLikes() {
        assertNull(truckLikeRepository.findByMemberIdAndTruckId(member.getId(), truck.getId()));
    }
}
