package com.barowoori.foodpinbackend.event.query;

import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventApplicationList;
import com.barowoori.foodpinbackend.event.query.application.EventApplicationListService;
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

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class EventApplicationListServiceTests {
    @Autowired
    private EventApplicationListService eventApplicationListService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventDateRepository eventDateRepository;
    @Autowired
    private EventApplicationRepository eventApplicationRepository;
    @Autowired
    private EventApplicationDateRepository eventApplicationDateRepository;
    @Autowired
    private EventTruckRepository eventTruckRepository;
    @Autowired
    private EventTruckDateRepository eventTruckDateRepository;
    @Autowired
    private TruckRepository truckRepository;

    Event event;

    @BeforeEach
    @Transactional
    void setUp() {
        event = Event.builder()
                .createdBy("user")
                .name("2월 행사")
                .isDeleted(Boolean.FALSE)
                .status(EventStatus.IN_PROGRESS)
                .build();
        event = eventRepository.save(event);
    }

    private EventDate createEventDate(LocalDate date) {
        EventDate eventDate = EventDate.builder()
                .event(event)
                .date(date)
                .build();
        return eventDateRepository.save(eventDate);
    }

    private void createApplications(Integer number, EventApplicationStatus status, EventDate eventDate) {
        for (int i = 0; i < number; i++) {
            Truck truck = Truck.builder()
                    .name("바로우리 " + i)
                    .isDeleted(Boolean.FALSE)
                    .build();
            truck = truckRepository.save(truck);
            EventApplication eventApplication = EventApplication.builder()
                    .truck(truck)
                    .event(event)
                    .status(status)
                    .isRead(Boolean.TRUE)
                    .build();
            eventApplication = eventApplicationRepository.save(eventApplication);

            EventApplicationDate eventApplicationDate = EventApplicationDate.builder()
                    .eventApplication(eventApplication)
                    .eventDate(eventDate)
                    .build();
            eventApplicationDate = eventApplicationDateRepository.save(eventApplicationDate);
        }
    }

    private EventTruck createEventTruck(String truckName, EventTruckStatus status, EventDate eventDate) {
        Truck truck = Truck.builder()
                .name(truckName)
                .isDeleted(Boolean.FALSE)
                .build();
        truck = truckRepository.save(truck);
        EventTruck eventTruck = EventTruck.builder()
                .truck(truck)
                .event(event)
                .status(status)
                .build();
        eventTruck = eventTruckRepository.save(eventTruck);

        EventTruckDate eventTruckDate = EventTruckDate.builder()
                .eventTruck(eventTruck)
                .eventDate(eventDate)
                .build();
        eventTruckDate = eventTruckDateRepository.save(eventTruckDate);

        return eventTruck;
    }


    @Nested
    @DisplayName("대기 지원자 목록 조회")
    class GetPendingApplications {
        private Page<EventApplicationList.EventPendingApplication> result;
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());

        @BeforeEach
        void setUp() {
            EventDate eventDate = createEventDate(LocalDate.of(2025, 3, 5));
            createApplications(3, EventApplicationStatus.PENDING, eventDate);
            result = eventApplicationListService.findPendingEventApplications(event.getId(), pageable);
        }

        @Test
        @Transactional
        @DisplayName("최신순으로 정렬되어야 한다")
        void OrderByCreatedAt() {
            assertEquals("바로우리 2", result.stream().findFirst().get().getTruck().getName());
        }

        @Test
        @Transactional
        @DisplayName("행사 주최자 읽음 여부가 나와야 한다")
        void isRead() {
            assertNotNull(result.stream().findFirst().get().getIsRead());
        }
    }

    @Nested
    @DisplayName("선정 지원자 목록 조회")
    class GetSelectedApplications {
        private Page<EventApplicationList.EventSelectedApplication> result;
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());

        @BeforeEach
        void setUp() {
            EventDate eventDate = createEventDate(LocalDate.of(2025, 3, 5));
            createEventTruck("바로우리 1", EventTruckStatus.PENDING, eventDate);
            createEventTruck("바로우리 2", EventTruckStatus.CONFIRMED, eventDate);
            createEventTruck("바로우리 3", EventTruckStatus.REJECTED, eventDate);

        }

        @Test
        @Transactional
        @DisplayName("답변대기중> 참여확정> 참여불가 순으로 정렬되어야 한다")
        void OrderBy() {
            result = eventApplicationListService.findSelectedEventApplications(event.getId(), "ALL", pageable);
            assertEquals(EventTruckStatus.PENDING, result.stream().findFirst().get().getStatus());
        }

        @Test
        @Transactional
        @DisplayName("status가 ALL이면 모든 선정된 지원자 목록이 나와야 한다")
        void WhenStatusIsALL() {
            result = eventApplicationListService.findSelectedEventApplications(event.getId(), "ALL", pageable);
            assertEquals(3, result.getTotalElements());
        }

        @Test
        @Transactional
        @DisplayName("status가 COMFIRMED이면 선정된 지원자 목록 중 참여 확정인 것만 나와야 한다")
        void WhenStatusIsCOMFIRMED() {
            result = eventApplicationListService.findSelectedEventApplications(event.getId(), EventTruckStatus.CONFIRMED.toString(), pageable);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @Transactional
        @DisplayName("status가 이면 선정된 지원자 목록 중 답변대기중인 것만 나와야 한다")
        void WhenStatusIsPENDING() {
            result = eventApplicationListService.findSelectedEventApplications(event.getId(), EventTruckStatus.PENDING.toString(), pageable);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @Transactional
        @DisplayName("status가 이면 선정된 지원자 목록 중 참여불가인 것만 나와야 한다")
        void WhenStatusIsREJECTED() {
            result = eventApplicationListService.findSelectedEventApplications(event.getId(), EventTruckStatus.REJECTED.toString(), pageable);
            assertEquals(1, result.getTotalElements());
        }
    }
}
