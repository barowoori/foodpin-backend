package com.barowoori.foodpinbackend.truck.query;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.model.TruckLike;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.TruckLikeRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.*;
import com.barowoori.foodpinbackend.truck.command.domain.repository.dto.TruckDetail;
import com.barowoori.foodpinbackend.truck.query.application.TruckDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TruckDetailServiceTests {
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TruckPhotoRepository truckPhotoRepository;
    @Autowired
    private TruckLikeRepository truckLikeRepository;
    @Autowired
    private TruckMenuRepository truckMenuRepository;
    @Autowired
    private TruckMenuPhotoRepository truckMenuPhotoRepository;
    @Autowired
    private TruckDocumentRepository truckDocumentRepository;
    @Autowired
    private TruckManagerRepository truckManagerRepository;

    @Autowired
    private TruckDetailService truckDetailService;

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
        //트럭 사진 필수
        for (int i = 0; i < 5; i++) {
            TruckPhoto truckPhoto = TruckPhoto.builder()
                    .path("path" + i)
                    .truck(truck)
                    .build();
            truckPhotoRepository.save(truckPhoto);
        }
        //트럭 메뉴랑 메뉴 사진 필수
        for (int i = 0; i < 5; i++) {
            TruckMenu truckMenu = TruckMenu.builder()
                    .name("떡볶이" + i)
                    .description("맛있다")
                    .price(1000)
                    .truck(truck)
                    .build();
            truckMenuRepository.save(truckMenu);
            for (int j = 0; j < 5; j++) {
                TruckMenuPhoto truckMenuPhoto = TruckMenuPhoto.builder()
                        .truckMenu(truckMenu)
                        .path("path" + j)
                        .build();
                truckMenuPhotoRepository.save(truckMenuPhoto);
            }
        }
    }


    @Test
    @DisplayName("트럭 사진은 함께 조회되어야 한다")
    void When_ExistTruckPhoto() {
        TruckDetail truckDetail = truckDetailService.getTruckDetail(member.getId(), truck.getId());
        assertEquals(truck.getId(), truckDetail.getTruck().getId());
        assertEquals(5, truckDetail.getTruck().getPhotos().size());
    }

    @Test
    @DisplayName("트럭 메뉴는 함께 조회되어야 한다")
    void When_ExistTruckMenu() {
        TruckDetail truckDetail = truckDetailService.getTruckDetail(member.getId(), truck.getId());
        assertEquals(5, truckDetail.getMenus().size());
        assertEquals(5, truckDetail.getMenus().get(0).getPhotos().size());
    }


    @Test
    @DisplayName("좋아요를 눌렀을 때 true를 반환해야 한다")
    void When_TruckLike_Then_ReturnTrue() {
        TruckLike truckLike = TruckLike.builder()
                .truck(truck)
                .member(member)
                .build();
        truckLike = truckLikeRepository.save(truckLike);

        TruckDetail truckDetail = truckDetailService.getTruckDetail(member.getId(), truck.getId());
        assertTrue(truckDetail.getIsLike());
    }

    @Test
    @Transactional
    @DisplayName("좋아요가 없을 때 false를 반환해야 한다")
    void When_NotTruckLike_Then_ReturnFalse() {
        TruckDetail truckDetail = truckDetailService.getTruckDetail(member.getId(), truck.getId());
        assertFalse(truckDetail.getIsLike());
    }

    @Test
    @Transactional
    @DisplayName("서류가 있을 때 서류 타입을 반환한다")
    void When_ExistDocument(){
        TruckDocument truckDocument = TruckDocument.builder()
                .type(DocumentType.BUSINESS_LICENSE)
                .path("path")
                .truck(truck)
                .build();
        truckDocument = truckDocumentRepository.save(truckDocument);
        TruckDetail truckDetail = truckDetailService.getTruckDetail(member.getId(), truck.getId());
        assertEquals(1, truckDetail.getDocuments().size());
    }

    @Test
    @Transactional
    @DisplayName("트럭 관리자에 속하지 않은 사람이 조회할 경우 수정, 삭제 불가하다")
    void When_NotInTruckManager_Then_NotAllowUpdateAndDelete(){
        TruckDetail truckDetail = truckDetailService.getTruckDetail(member.getId(), truck.getId());
        assertFalse(truckDetail.getIsAvailableDelete());
        assertFalse(truckDetail.getIsAvailableUpdate());
    }

    @Test
    @DisplayName("트럭 운영자가 조회할 경우 수정이 가능하지만 삭제는 불가하다")
    void When_TruckManagerRoleIsMember_Then_AllowUpdateAndNotAllowDelete(){
        TruckManager truckManager = TruckManager.builder()
                .truck(truck)
                .member(member)
                .role(TruckManagerRole.MEMBER)
                .build();
        truckManagerRepository.save(truckManager);
        TruckDetail truckDetail = truckDetailService.getTruckDetail(member.getId(), truck.getId());

        assertTrue(truckDetail.getIsAvailableUpdate());
        assertFalse(truckDetail.getIsAvailableDelete());
    }

    @Test
    @DisplayName("트럭 소유자가 조회할 경우 수정과 삭제가 가능하다")
    void When_TruckManagerRoleIsOwner_Then_AllowUpdateAndDelete(){
        TruckManager truckManager = TruckManager.builder()
                .truck(truck)
                .member(member)
                .role(TruckManagerRole.OWNER)
                .build();
        truckManagerRepository.save(truckManager);
        TruckDetail truckDetail = truckDetailService.getTruckDetail(member.getId(), truck.getId());

        assertTrue(truckDetail.getIsAvailableUpdate());
        assertTrue(truckDetail.getIsAvailableDelete());
    }
}
