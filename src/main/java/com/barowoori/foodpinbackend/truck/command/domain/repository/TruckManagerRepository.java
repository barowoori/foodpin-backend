package com.barowoori.foodpinbackend.truck.command.domain.repository;

import com.barowoori.foodpinbackend.member.command.domain.model.Member;
import com.barowoori.foodpinbackend.truck.command.domain.model.TruckManager;
import com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl.TruckManagerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TruckManagerRepository extends JpaRepository<TruckManager, String>, TruckManagerRepositoryCustom {
    TruckManager findByTruckIdAndMemberId(String truckId, String memberId);
    List<TruckManager> findAllByMember(Member member);
}
