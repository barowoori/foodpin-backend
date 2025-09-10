package com.barowoori.foodpinbackend.event.command.domain.service;

import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.model.EventDate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
public class EventDateCalculator {

    public static LocalDate getMinDate(Event event) {
        return event.getEventDates().stream()
                .map(EventDate::getDate)
                .min(Comparator.naturalOrder()).orElse(null);
    }

    public static LocalDate getMaxDate(Event event) {
        return event.getEventDates().stream()
                .map(EventDate::getDate)
                .max(Comparator.naturalOrder()).orElse(null);
    }
}
