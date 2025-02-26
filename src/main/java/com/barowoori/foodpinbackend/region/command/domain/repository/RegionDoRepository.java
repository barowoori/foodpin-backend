package com.barowoori.foodpinbackend.region.command.domain.repository;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionDo;
import com.barowoori.foodpinbackend.region.command.domain.repository.querydsl.RegionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionDoRepository extends JpaRepository<RegionDo, String>, RegionRepositoryCustom {
}
