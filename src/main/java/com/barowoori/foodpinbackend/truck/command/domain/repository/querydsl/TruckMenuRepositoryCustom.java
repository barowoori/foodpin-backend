package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;

import java.util.List;

public interface TruckMenuRepositoryCustom {
    List<TruckMenu> getMenuListWithPhotoByTruckId(String truckId);
}
