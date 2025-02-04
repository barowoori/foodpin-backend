package com.barowoori.foodpinbackend.region.command.domain.repository;

import com.barowoori.foodpinbackend.region.command.domain.model.RegionGun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionGunRepository extends JpaRepository<RegionGun, String> {
}
