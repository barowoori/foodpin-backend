package com.barowoori.foodpinbackend.region.command.domain.repository;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionGu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionGuRepository extends JpaRepository<RegionGu, String> {
}
