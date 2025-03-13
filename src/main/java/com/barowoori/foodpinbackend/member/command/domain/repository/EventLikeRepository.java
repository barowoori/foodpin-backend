package com.barowoori.foodpinbackend.member.command.domain.repository;

import com.barowoori.foodpinbackend.member.command.domain.model.EventLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventLikeRepository extends JpaRepository<EventLike, String> {
    EventLike findByMemberIdAndEventId(String memberId, String eventId);
    List<EventLike> findByEventId(String eventId);
}
