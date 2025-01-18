package com.barowoori.foodpinbackend.truck.command.domain.repository.querydsl;

import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;

public interface TruckRepositoryCustom {
    Truck getTruckWithPhotoById(String id);
}
