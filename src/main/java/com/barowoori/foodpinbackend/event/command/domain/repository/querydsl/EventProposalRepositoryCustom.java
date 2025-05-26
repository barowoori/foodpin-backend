package com.barowoori.foodpinbackend.event.command.domain.repository.querydsl;

import java.util.List;

public interface EventProposalRepositoryCustom {
    List<String> findEventIdsProposedToTruckByMember(String truckId, String memberId);
}
