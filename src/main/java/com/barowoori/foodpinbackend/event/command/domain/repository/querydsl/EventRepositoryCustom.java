package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.common.dto.MemberFcmInfoDto;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.region.command.domain.model.RegionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventRepositoryCustom {
    Page<Event> findEventListByFilter(String searchTerm, Map<RegionType, List<String>> regionIds,
                                      LocalDate startDate, LocalDate endDate,
                                      List<String> categoryCodes, Pageable pageable);
    Event findEventDetail(String eventId);
    Page<Event> findLikeEventListByFilter(String memberId, String searchTerm, Map<RegionType, List<String>> regionIds,
                                          LocalDate startDate, LocalDate endDate,
                                          List<String> categoryCodes, Pageable pageable);

    List<Event> findEventsEndedAndStillSelecting(LocalDateTime now);

    Page<Event> findProgressEventManageList(String memberId, String status, Pageable pageable);
    Page<Event> findCompletedEventManageList(String memberId, String status, Pageable pageable);
    MemberFcmInfoDto findEventCreatorFcmInfo(String eventId);
    List<Event> findAvailableEventListForProposal(String memberId);
}
