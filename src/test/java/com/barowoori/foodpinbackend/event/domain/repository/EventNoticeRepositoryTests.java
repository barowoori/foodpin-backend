package com.barowoori.foodpinbackend.event.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventNotice;
import com.barowoori.foodpinbackend.event.command.domain.model.EventNoticeView;
import com.barowoori.foodpinbackend.event.command.domain.model.EventStatus;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventNoticeRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventNoticeViewRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                .status(EventStatus.IN_PROGRESS)
                .build();
        event = eventRepository.save(event);
    }

    private EventNotice createEventNotice(Event event, String title, String context, Boolean isDeleted){
        EventNotice eventNotice = EventNotice.builder()
                .event(event)
                .title(title)
                .context(context)
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
                createEventNotice(event, "제목 "+ i, "내용", Boolean.FALSE);
            }
            createEventNotice(event, "제목 "+ 3, "내용", Boolean.TRUE);
        }
        @Test
        @Transactional
        @DisplayName("공지사항 목록에는 삭제된 공지사항은 포함하지 않는다")
        void Then_DeletedEventNoticeNotContains(){
            Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
            Page<EventNotice> result = eventNoticeRepository.findEventNoticeListByEventId(event.getId(), pageable);
            assertTrue(result.stream().noneMatch(eventNotice -> eventNotice.getIsDeleted().equals(Boolean.TRUE)));
        }

        @Test
        @Transactional
        @DisplayName("공지사항 목록은 최신순으로 정렬된다")
        void Then_OrderByCreatedAtDesc(){
            Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
            Page<EventNotice> result = eventNoticeRepository.findEventNoticeListByEventId(event.getId(), pageable);
            assertEquals("제목 2", result.stream().findFirst().get().getTitle());
        }
    }
}
