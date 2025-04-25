package com.barowoori.foodpinbackend.event.application.service;

import com.barowoori.foodpinbackend.event.command.application.service.EventStatusUpdater;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventDateRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRecruitDetailRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
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

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventRecruitDetailRepository eventRecruitDetailRepository;
    @Autowired
    private EventStatusUpdater eventStatusUpdater;
    @Autowired
    private EventDateRepository eventDateRepository;

    @Test
    public void testUpdateSelecting() {
        // given
        Event event = Event.builder()
                .name("테스트 이벤트")
                .createdBy("test")
                .status(EventStatus.RECRUITING)
                .description("desc")
                .guidelines("guideline")
                .isDeleted(false)
                .submissionEmail("test@example.com")
                .documentSubmissionTarget(EventDocumentSubmissionTarget.ALL_APPLICANTS)
                .build();

        eventRepository.save(event);

        EventRecruitDetail detail = EventRecruitDetail.builder()
                .recruitEndDateTime(LocalDateTime.now().minusMinutes(10))
                .recruitCount(10)
                .entryFee(10000)
                .electricitySupportAvailability(true)
                .generatorRequirement(false)
                .event(event)
                .build();

        eventRecruitDetailRepository.save(detail);
        event.initEventRecruitDetail(detail);

        eventRepository.save(event);

        // when
        eventStatusUpdater.updateSelecting();

        // then
        Event updated = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(EventStatus.SELECTING);
    }

    @Test
    public void testUpdateToInProgress() {
        Event event = Event.builder()
                .name("진행 중 이벤트")
                .createdBy("test")
                .status(EventStatus.SELECTING)
                .description("desc")
                .guidelines("guideline")
                .isDeleted(false)
                .submissionEmail("test@example.com")
                .documentSubmissionTarget(EventDocumentSubmissionTarget.ALL_APPLICANTS)
                .build();

        eventRepository.save(event);

        EventDate ed = EventDate.builder()
                .date(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .event(event)
                .build();

        eventDateRepository.save(ed);
        event.getEventDates().add(ed);

        // when
        eventStatusUpdater.updateInProgressAndCompleted();

        // then
        Event updated = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(EventStatus.IN_PROGRESS);
    }

    @Test
    public void testUpdateToCompleted() {
        Event event = Event.builder()
                .name("종료된 이벤트")
                .createdBy("test")
                .status(EventStatus.IN_PROGRESS)
                .description("desc")
                .guidelines("guideline")
                .isDeleted(false)
                .submissionEmail("test@example.com")
                .documentSubmissionTarget(EventDocumentSubmissionTarget.ALL_APPLICANTS)
                .build();

        eventRepository.save(event);

        EventDate ed = EventDate.builder()
                .date(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .event(event)
                .build();

        eventDateRepository.save(ed);
        event.getEventDates().add(ed);

        // when
        eventStatusUpdater.updateInProgressAndCompleted();

        // then
        Event updated = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(EventStatus.COMPLETED);
    }
}
