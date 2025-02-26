package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.truck.command.domain.model.TruckMenu;

import java.util.List;
import java.util.Map;

public interface TruckMenuRepositoryCustom {
    List<TruckMenu> getMenuListWithPhotoByTruckId(String truckId);
    Map<String, List<String>> getMenuNamesByTruckIds(List<String> truckIds);
}
