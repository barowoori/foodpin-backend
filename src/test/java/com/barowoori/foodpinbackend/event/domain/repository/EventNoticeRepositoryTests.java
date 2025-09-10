package com.barowoori.foodpinbackend.event.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventNoticeRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventNoticeViewRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class EventNoticeRepositoryTests {
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventNoticeRepository eventNoticeRepository;
    @Autowired
    private EventTruckRepository eventTruckRepository;
    @Autowired
    private EventNoticeViewRepository eventNoticeViewRepository;
    @Autowired
    private MemberRepository memberRepository;

    Member member;
    Event event;

    @BeforeEach
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
                .build();
        event = eventRepository.save(event);
    }

    private EventNotice createEventNotice(Event event, String title, String content, Boolean isDeleted) {
        EventNotice eventNotice = EventNotice.builder()
                .event(event)
                .title(title)
                .content(content)
                .isDeleted(isDeleted)
                .build();
        return eventNoticeRepository.save(eventNotice);
    }

    @Nested
    @DisplayName("행사 공지사항 목록 조회")
    class GetEventNoticeList {
        @BeforeEach
        void setUp() {
            for (int i = 0; i < 3; i++) {
                createEventNotice(event, "제목 " + i, "내용", Boolean.FALSE);
            }
            createEventNotice(event, "제목 " + 3, "내용", Boolean.TRUE);
        }

        @Test
        @Transactional
        @DisplayName("공지사항 목록에는 삭제된 공지사항은 포함하지 않는다")
        void Then_DeletedEventNoticeNotContains() {
            Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
            Page<EventNotice> result = eventNoticeRepository.findEventNoticeListByEventId(event.getId(), pageable);
            assertTrue(result.stream().noneMatch(eventNotice -> eventNotice.getIsDeleted().equals(Boolean.TRUE)));
        }

        @Test
        @Transactional
        @DisplayName("공지사항 목록은 최신순으로 정렬된다")
        void Then_OrderByCreatedAtDesc() {
            Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
            Page<EventNotice> result = eventNoticeRepository.findEventNoticeListByEventId(event.getId(), pageable);
            assertEquals("제목 2", result.stream().findFirst().get().getTitle());
        }
    }

    @Nested
    @DisplayName("행사 공지사항 상세 조회")
    class GetEventNoticeDetail {
        EventNotice eventNotice;

        @BeforeEach
        @Transactional
        void setUp() {
            Truck truck = Truck.builder()
                    .name("바로우리")
                    .isDeleted(Boolean.FALSE)
                    .build();
            truck = truckRepository.save(truck);

            EventTruck eventTruck = EventTruck.builder()
                    .event(event)
                    .truck(truck)
                    .build();
            eventTruck = eventTruckRepository.save(eventTruck);

            eventNotice = createEventNotice(event, "제목", "내용", Boolean.FALSE);

            EventNoticeView eventNoticeView = EventNoticeView.builder()
                    .eventNotice(eventNotice)
                    .eventTruck(eventTruck)
                    .build();
            eventNoticeView = eventNoticeViewRepository.save(eventNoticeView);
        }

        @Test
        @DisplayName("행사 공지사항을 행사 생성자가 조회할 때는 읽은 사람도 조회되어야 한다")
        void When_ReadCreator_Then_ContainsViews() {
            EventNotice result = eventNoticeRepository.findEventNoticeForCreator(eventNotice.getId());
            assertTrue(!result.getViews().isEmpty());
        }
    }
}
