package com.barowoori.foodpinbackend.document.command.application.service.emailEvent;


import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.truck.command.domain.model.Truck;
import lombok.Getter;

@Getter
public class EventAppliedTruckDocumentSubmissionEvent {
    private final Event event;
    private final Truck truck;

    public EventAppliedTruckDocumentSubmissionEvent(Event event, Truck truck) {
        this.event = event;
        this.truck = truck;
    }
}
