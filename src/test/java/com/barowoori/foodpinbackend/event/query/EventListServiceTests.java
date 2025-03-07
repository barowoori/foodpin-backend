package com.barowoori.foodpinbackend.event.query;

import com.barowoori.foodpinbackend.category.command.domain.model.Category;
import com.barowoori.foodpinbackend.category.command.domain.repository.CategoryRepository;
import com.barowoori.foodpinbackend.event.command.domain.model.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.*;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.EventList;
import com.barowoori.foodpinbackend.event.query.application.EventListService;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionDo;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionSi;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGuRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGunRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionSiRepository;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class EventListServiceTests {
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
    private EventListService eventListService;
    @Autowired
    private EventDateRepository eventDateRepository;

    Event event;
    RegionSi yongIn;
    RegionSi anyang;


    @BeforeEach
    void setUp() {
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

        Event event1 = Event.builder()
                .createdBy("user")
                .name("2월 행사2")
                .isDeleted(Boolean.FALSE)
                .status(EventStatus.IN_PROGRESS)
                .build();
        event1 = eventRepository.save(event1);

        EventRecruitDetail eventRecruitDetail1 = EventRecruitDetail.builder()
                .recruitEndDateTime(LocalDateTime.of(2025, 3, 3, 0, 0))
                .recruitCount(4)
                .event(event1)
                .build();
        eventRecruitDetail1 = eventRecruitDetailRepository.save(eventRecruitDetail1);
        event1.initEventRecruitDetail(eventRecruitDetail1);

        EventView view1 = EventView.builder()
                .views(0)
                .event(event1)
                .build();
        view1 = eventViewRepository.save(view1);
        event1.initEventView(view1);

        RegionDo gyeonggi = new RegionDo.Builder().addName("경기도").build();
        gyeonggi = regionDoRepository.save(gyeonggi);

        yongIn = new RegionSi.Builder().addRegionDo(gyeonggi).addName("용인시").build();
        yongIn = regionSiRepository.save(yongIn);

        anyang = new RegionSi.Builder().addRegionDo(gyeonggi).addName("안양시").build();
        anyang = regionSiRepository.save(anyang);
    }

    @Nested
    @Transactional
    @DisplayName("필터링 테스트")
    class Filtering {
        Event deletedEvent;

        @BeforeEach
        void setUp() {
            Category category = Category.builder()
                    .name("중식")
                    .code("C01")
                    .build();
            category = categoryRepository.save(category);
            Category category1 = Category.builder()
                    .name("한식")
                    .code("C02")
                    .build();
            category1 = categoryRepository.save(category1);

            deletedEvent = Event.builder()
                    .name("삭제된 행사")
                    .createdBy("user")
                    .isDeleted(Boolean.TRUE)
                    .status(EventStatus.IN_PROGRESS)
                    .build();
            deletedEvent = eventRepository.save(deletedEvent);

            EventCategory eventCategory = EventCategory.builder()
                    .category(category)
                    .event(event)
                    .build();
            eventCategory = eventCategoryRepository.save(eventCategory);

            EventRegion eventRegion = EventRegion.builder()
                    .regionId(yongIn.getId())
                    .regionType(RegionType.REGION_SI)
                    .event(event)
                    .build();
            eventRegion = eventRegionRepository.save(eventRegion);

            EventDate eventDate = EventDate.builder()
                    .date(LocalDate.of(2025, 2, 27))
                    .event(event)
                    .build();
            eventDate = eventDateRepository.save(eventDate);

            EventDate eventDate1 = EventDate.builder()
                    .date(LocalDate.of(2025, 4, 1))
                    .event(event)
                    .build();
            eventDate1 = eventDateRepository.save(eventDate1);
        }

        @Test
        @Transactional
        @DisplayName("필터링 걸지 않았을 때는 삭제되지 않은 행사 전체가 조회되어야 한다")
        void When_NotFilter_Then_GetAllEventList() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            assertThat(eventListService.findEventList(null, null, null, null, null, pageable).get()
                    .noneMatch(event -> event.getId().equals(deletedEvent)));
        }

        @Test
        @Transactional
        @DisplayName("카테고리 필터링이 걸려있으면 헤당 카테고리에 속하는 행사들이 조회되어야 한다")
        void When_CategoryFiltering() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<EventList> result = eventListService.findEventList(null, null, null, null, List.of("C01"), pageable);
            assertEquals(1, result.getNumberOfElements());
            assertEquals(event.getName(), result.get().findFirst().get().getName());
        }

        @Test
        @Transactional
        @DisplayName("기간 필터링이 걸려있고 해당 기간 내에 속하는 행사는 조회되어야 한다")
        void When_BetweenEventDate() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<EventList> result = eventListService.findEventList(null, null, LocalDate.of(2025,2,27),  LocalDate.of(2025,2,27), null, pageable);
            assertEquals(1, result.getNumberOfElements());
            assertEquals(event.getName(), result.get().findFirst().get().getName());
        }
    }

    @Nested
    @DisplayName("정렬 테스트")
    class OrderBy {
        @Test
        @Transactional
        @DisplayName("정렬 기준이 최신순일 경우 행사 생성일자 기준으로 내림차순 정렬된다")
        void When_OrderByCreatedAt() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<EventList> result = eventListService.findEventList(null, null, null,  null, null, pageable);
            assertEquals("2월 행사2", result.stream().findFirst().get().getName());
            result.forEach(event -> System.out.println(event.getName()));
        }
        @Test
        @Transactional
        @DisplayName("정렬 기준이 조회순일 경우 행사 조회수 기준으로 내림차순 정렬된다")
        void When_OrderByViews() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "views"));
            Page<EventList> result = eventListService.findEventList(null, null, null,  null, null, pageable);
            assertEquals(event.getName(), result.stream().findFirst().get().getName());
            result.forEach(event -> System.out.println(event.getName()));
        }
        @Test
        @Transactional
        @DisplayName("정렬 기준이 지원순일 경우 행사 지원자수 기준으로 내림차순 정렬된다")
        void When_OrderByApplicant() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "applicant"));
            Page<EventList> result = eventListService.findEventList(null, null, null,  null, null, pageable);
            assertEquals(event.getName(), result.stream().findFirst().get().getName());
            result.forEach(event -> System.out.println(event.getName()));
        }
    }

}
