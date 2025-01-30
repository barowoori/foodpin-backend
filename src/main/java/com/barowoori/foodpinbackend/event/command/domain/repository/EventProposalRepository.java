package com.barowoori.foodpinbackend.event.command.domain.repository;

import com.barowoori.foodpinbackend.event.command.domain.model.EventProposal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventProposalRepository extends JpaRepository<EventProposal, String> {
}
