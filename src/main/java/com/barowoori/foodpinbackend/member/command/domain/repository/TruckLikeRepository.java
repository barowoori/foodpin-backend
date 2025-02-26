package com.barowoori.foodpinbackend.member.command.domain.repository;

import com.barowoori.foodpinbackend.member.command.domain.model.TruckLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckLikeRepository extends JpaRepository<TruckLike, String> {
    TruckLike findByMemberIdAndTruckId(String memberId, String truckId);
}
