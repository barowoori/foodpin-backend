package com.barowoori.foodpinbackend.event.application.service;

import com.barowoori.foodpinbackend.event.command.application.service.EventStatusUpdater;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventApplicationRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventDateRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRecruitDetailRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckDateRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventTruckRepository;
import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginInfo;
import com.barowoori.foodpinbackend.member.command.domain.model.SocialLoginType;
import com.barowoori.foodpinbackend.member.command.domain.repository.MemberRepository;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import com.barowoori.foodpinbackend.truck.command.domain.repository.TruckRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class EventStatusUpdaterTests {

    @Autowired private EventRepository eventRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private EventRecruitDetailRepository recruitDetailRepository;
    @Autowired private EventDateRepository eventDateRepository;
    @Autowired private EventApplicationRepository eventApplicationRepository;
    @Autowired private EventTruckRepository eventTruckRepository;
    @Autowired private EventTruckDateRepository eventTruckDateRepository;
    @Autowired private TruckRepository truckRepository;
    @Autowired private EventStatusUpdater eventStatusUpdater;

    @Test
    @Transactional
    void testCloseRecruitingEventsByDeadline() {
        // given
        Event event = saveBasicEvent();

        EventDate eventDate = eventDateRepository.save(EventDate.builder()
                .event(event)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .build());
        event.getEventDates().add(eventDate);

        EventRecruitDetail detail = EventRecruitDetail.builder()
                .event(event)
                .recruitEndDateTime(LocalDateTime.now().minusMinutes(1))
                .recruitingStatus(EventRecruitingStatus.RECRUITING)
                .recruitCount(5)
                .applicantCount(1)
                .selectedCount(0)
                .isSelecting(true)
                .generatorRequirement(false)
                .electricitySupportAvailability(true)
                .build();
        recruitDetailRepository.save(detail);
        event.initEventRecruitDetail(detail);

        EventApplication application = eventApplicationRepository.save(
                EventApplication.builder()
                        .event(event)
                        .status(EventApplicationStatus.PENDING)
                        .isRead(false)
                        .build()
        );

        // when
        eventStatusUpdater.updateEventStatuses();

        // then
        Event updated = eventRepository.findById(event.getId()).orElseThrow();
        EventApplication updatedApplication = eventApplicationRepository.findById(application.getId()).orElseThrow();

        assertThat(updated.getRecruitDetail().getRecruitingStatus()).isEqualTo(EventRecruitingStatus.RECRUITMENT_CLOSED);
        assertThat(updatedApplication.getStatus()).isEqualTo(EventApplicationStatus.REJECTED);
    }


    @Test
    @Transactional
    void testCloseSelectingEventsByEndDate() {
        // given
        Event event = saveBasicEvent();

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
                .recruitingStatus(EventRecruitingStatus.RECRUITMENT_CLOSED)
                .recruitCount(5)
                .applicantCount(1)
                .selectedCount(1)
                .isSelecting(true)
                .generatorRequirement(false)
                .electricitySupportAvailability(true)
                .build();
        recruitDetailRepository.save(detail);
        event.initEventRecruitDetail(detail);

        Truck truck = truckRepository.save(Truck.builder()
                .name("테스트 푸드트럭")
                .description("desc")
                .build());

        EventTruck eventTruck = eventTruckRepository.save(EventTruck.builder()
                .event(event)
                .truck(truck)
                .status(EventTruckStatus.PENDING)
                .build());

        EventTruckDate eventTruckDate = eventTruckDateRepository.save(
                EventTruckDate.builder()
                        .eventDate(ed)
                        .eventTruck(eventTruck)
                        .build()
        );
        eventTruck.getDates().add(eventTruckDate);

        // when
        eventStatusUpdater.updateEventStatuses();

        // then
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        EventTruck updatedEventTruck = eventTruckRepository.findById(eventTruck.getId()).orElseThrow();

        assertThat(updatedEvent.getRecruitDetail().getIsSelecting()).isFalse();
        assertThat(updatedEventTruck.getStatus()).isEqualTo(EventTruckStatus.REJECTED);
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
