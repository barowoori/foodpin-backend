package com.barowoori.foodpinbackend.event.query;

import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitDetail;
import com.barowoori.foodpinbackend.event.command.domain.model.EventStatus;
import com.barowoori.foodpinbackend.event.command.domain.model.EventView;
import com.barowoori.foodpinbackend.event.command.domain.repository.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventDetail;
import com.barowoori.foodpinbackend.event.query.application.EventDetailService;
import com.barowoori.foodpinbackend.member.command.domain.model.*;
import com.barowoori.foodpinbackend.member.command.domain.repository.EventLikeRepository;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionDo;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionSi;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGuRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGunRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionSiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class EventDetailServiceTests {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EventDetailService eventDetailService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventRecruitDetailRepository eventRecruitDetailRepository;
    @Autowired
    private EventRegionRepository eventRegionRepository;
    @Autowired
    private EventCategoryRepository eventCategoryRepository;
    @Autowired
    private EventViewRepository eventViewRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RegionDoRepository regionDoRepository;
    @Autowired
    private RegionSiRepository regionSiRepository;
    @Autowired
    private RegionGuRepository regionGuRepository;
    @Autowired
    private RegionGunRepository regionGunRepository;
    @Autowired
    private EventLikeRepository eventLikeRepository;

    Member member;
    Event event;
    RegionSi yongIn;
    RegionSi anyang;

    @BeforeEach
    @Transactional
    void setUp() {
        member = Member.builder()
                .email("email")
                .phone("01012341234")
                .nickname("nickname")
                .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                .build();
        member = memberRepository.save(member);

        event = Event.builder()
                .createdBy("user")
                .name("2월 행사")
                .isDeleted(Boolean.FALSE)
                .status(EventStatus.IN_PROGRESS)
                .build();
        event = eventRepository.save(event);

        EventRecruitDetail eventRecruitDetail = EventRecruitDetail.builder()
                .recruitEndDateTime(LocalDateTime.of(2025, 3, 3, 0, 0))
                .recruitCount(4)
                .event(event)
                .build();
        eventRecruitDetail = eventRecruitDetailRepository.save(eventRecruitDetail);
        event.initEventRecruitDetail(eventRecruitDetail);

        EventView view = EventView.builder()
                .views(100)
                .event(event)
                .build();
        view = eventViewRepository.save(view);
        event.initEventView(view);

        RegionDo gyeonggi = new RegionDo.Builder().addName("경기도").build();
        gyeonggi = regionDoRepository.save(gyeonggi);

        yongIn = new RegionSi.Builder().addRegionDo(gyeonggi).addName("용인시").build();
        yongIn = regionSiRepository.save(yongIn);

        anyang = new RegionSi.Builder().addRegionDo(gyeonggi).addName("안양시").build();
        anyang = regionSiRepository.save(anyang);
    }

    @Test
    @Transactional
    @DisplayName("좋아요를 눌렀을 때 true를 반환해야 한다")
    void When_EventLike_Then_ReturnTrue() {
        EventLike eventLike = EventLike.builder()
                .event(event)
                .member(member)
                .build();
        eventLike = eventLikeRepository.save(eventLike);

        EventDetail eventDetail = eventDetailService.getEventDetail(member.getId(), event.getId());
        assertTrue(eventDetail.getIsLike());
    }
}
