package com.barowoori.foodpinbackend.event.query.application;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventProposalRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.EventRepository;
import com.barowoori.foodpinbackend.event.command.domain.repository.dto.AvailableEventListForProposal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AvailableEventListForProposalService {
    private final EventRepository eventRepository;
    private final EventProposalRepository eventProposalRepository;

    public AvailableEventListForProposalService(EventRepository eventRepository,
                                                EventProposalRepository eventProposalRepository) {
        this.eventRepository = eventRepository;
        this.eventProposalRepository = eventProposalRepository;
    }

    @Transactional(readOnly = true)
    public List<AvailableEventListForProposal> getAvailableEventListForProposal(String memberId, String truckId) {
        List<Event> events = eventRepository.findAvailableEventListForProposal(memberId);
        List<String> proposalEventIds = eventProposalRepository.findEventIdsProposedToTruckByMember(truckId, memberId);
        return events.stream()
                .map(event -> AvailableEventListForProposal.of(event, proposalEventIds.contains(event.getId())))
                .toList();
    }
}
