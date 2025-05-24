package com.barowoori.foodpinbackend.event.application.service;

import com.barowoori.foodpinbackend.event.command.application.service.EventStatusUpdater;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventDateRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRecruitDetailRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class EventStatusUpdaterTests {

    @Autowired private EventRepository eventRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private EventRecruitDetailRepository recruitDetailRepository;
    @Autowired private EventDateRepository eventDateRepository;
    @Autowired private EventApplicationRepository eventApplicationRepository;
    @Autowired private EventStatusUpdater eventStatusUpdater;

    @Test
    void testCloseRecruitingEventsByDeadline() {
        // given
        Event event = saveBasicEvent();
        EventRecruitDetail detail = EventRecruitDetail.builder()
                .event(event)
                .recruitEndDateTime(LocalDateTime.now().minusDays(1))
                .recruitingStatus(EventRecruitingStatus.RECRUITING)
                .recruitCount(5)
                .applicantCount(0)
                .selectedCount(0)
                .isSelecting(true)
                .entryFee(0)
                .generatorRequirement(false)
                .electricitySupportAvailability(true)
                .build();
        recruitDetailRepository.save(detail);
        event.initEventRecruitDetail(detail);

        // when
        eventStatusUpdater.closeRecruitingEventsByDeadline();

        // then
        Event updated = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getRecruitDetail().getRecruitingStatus()).isEqualTo(EventRecruitingStatus.RECRUITMENT_CLOSED);
    }

    @Test
    void testCloseSelectingEventsByEndDate() {
        // given
        Member member = memberRepository.save(Member.builder()
                .email("email")
                .phone("01012341234")
                .nickname("nickname")
                .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                .build());
        Event event = eventRepository.save(Event.builder()
                .name("스케쥴러 테스트 이벤트")
                .createdBy(member.getId())
                .description("desc")
                .guidelines("guideline")
                .isDeleted(false)
                .submissionEmail("test@example.com")
                .documentSubmissionTarget(EventDocumentSubmissionTarget.ALL_APPLICANTS)
                .build());
        EventDate ed = eventDateRepository.save(EventDate.builder()
                .event(event)
                .date(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .build());
        event.getEventDates().add(ed);

        EventRecruitDetail detail = EventRecruitDetail.builder()
                .event(event)
                .recruitEndDateTime(LocalDateTime.now().minusDays(2))
                .recruitingStatus(EventRecruitingStatus.RECRUITING)
                .recruitCount(5)
                .applicantCount(0)
                .selectedCount(0)
                .isSelecting(true)
                .entryFee(0)
                .generatorRequirement(false)
                .electricitySupportAvailability(true)
                .build();
        recruitDetailRepository.save(detail);
        event.initEventRecruitDetail(detail);

        EventApplication app = eventApplicationRepository.save(EventApplication.builder()
                .event(event)
                .status(EventApplicationStatus.PENDING)
                .isRead(false)
                .build());

        // when
        eventStatusUpdater.closeSelectingEventsByEndDate();

        // then
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        EventApplication updatedApp = eventApplicationRepository.findById(app.getId()).orElseThrow();

        assertThat(updatedEvent.getRecruitDetail().getIsSelecting()).isFalse();
        assertThat(updatedApp.getStatus()).isEqualTo(EventApplicationStatus.REJECTED);
    }

    private Event saveBasicEvent() {
        Member memberBuilder = Member.builder()
                .email("email")
                .phone("01012341234")
                .nickname("nickname")
                .socialLoginInfo(new SocialLoginInfo(SocialLoginType.KAKAO, "id123"))
                .build();
        Member member = memberRepository.save(memberBuilder);
        return eventRepository.save(Event.builder()
                .name("스케쥴러 테스트 이벤트")
                .createdBy(member.getId())
                .description("desc")
                .guidelines("guideline")
                .isDeleted(false)
                .submissionEmail("test@example.com")
                .documentSubmissionTarget(EventDocumentSubmissionTarget.ALL_APPLICANTS)
                .build());
    }
}