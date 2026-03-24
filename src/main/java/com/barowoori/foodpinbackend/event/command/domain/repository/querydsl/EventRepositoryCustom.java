package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventRecruitingStatus;
import com.barowoori.foodpinbackend.event.command.domain.model.EventType;
import com.barowoori.foodpinbackend.event.command.domain.model.ExpectedParticipants;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.MemberForEventFcmInfoDto;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EventRepositoryCustom {
    Page<Event> findEventListByFilter(String searchTerm, Map<RegionType, List<String>> regionIds,
                                      LocalDate startDate, LocalDate endDate,
                                      List<String> categoryCodes,
                                      EventType type, ExpectedParticipants expectedParticipants, Set<TruckType> truckTypes, Boolean isCatering, List<EventRecruitingStatus> recruitingStatuses,
                                      Pageable pageable);

    Page<Event> findBackOfficeEventListByFilter(String searchTerm, Map<RegionType, List<String>> regionIds,
                                                LocalDate startDate, LocalDate endDate,
                                                List<String> categoryCodes,
                                                EventType type, ExpectedParticipants expectedParticipants, Set<TruckType> truckTypes, Boolean isCatering,
                                                LocalDate recruitEndDateFrom, LocalDate recruitEndDateTo,
                                                Pageable pageable);

    Event findEventDetail(String eventId);

    Page<Event> findLikeEventListByFilter(String memberId, String searchTerm, Map<RegionType, List<String>> regionIds,
                                          LocalDate startDate, LocalDate endDate,
                                          List<String> categoryCodes,
                                          EventType type, ExpectedParticipants expectedParticipants, Set<TruckType> truckTypes, Boolean isCatering, Pageable pageable);

    List<Event> findEndedEventsByIsSelecting(LocalDateTime now, Boolean isSelecting);

    Page<Event> findProgressEventManageList(String memberId, String status, Pageable pageable);

    Page<Event> findCompletedEventManageList(String memberId, String status, Pageable pageable);

    MemberFcmInfoDto findEventCreatorFcmInfo(String eventId);

    List<Event> findAvailableEventListForProposal(String memberId);

    List<MemberForEventFcmInfoDto> findSelectionNotEndedEventCreatorsFcmInfo();

    List<MemberForEventFcmInfoDto> findRecruitmentDeadlineSoonEventCreatorsFcmInfo();

    List<Event> findRecruitmentDeadlineSoonEvents();

    Long findCountRecruitingStatus(String memberId);

    Long findCountProgressStatus(String memberId);

    Long findCountEndStatus(String memberId);

    String getEventPhone(String eventId);
}
