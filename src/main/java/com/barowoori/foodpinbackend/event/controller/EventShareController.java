package com.barowoori.foodpinbackend.event.controller;

import com.barowoori.foodpinbackend.event.command.application.service.EventService;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.event.command.domain.service.EventDateCalculator;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
public class EventShareController {
    private final EventService eventService;
    private final ImageManager imageManager;

    public EventShareController(EventService eventService, ImageManager imageManager) {
        this.eventService = eventService;
        this.imageManager = imageManager;
    }

    @GetMapping("/share/event/{id}")
    public String shareEvent(@PathVariable String id, Model model) {
        Event event = eventService.getEventById(id);
        LocalDate startDate = EventDateCalculator.getMinDate(event);


        model.addAttribute("title", "푸드핀");
        model.addAttribute("description", event.getName() +" "+ calculateDDay(startDate));
        model.addAttribute("image", getImage(event.getEventMainPhotoUrl(imageManager)));
        model.addAttribute("url", "https://dev.barowoori.click/share/event/" + id);
        model.addAttribute("deepLink", "foodpin://events?eventId=" +id);
        return "share/default";
    }

    private String getImage(String eventImageUrl){
        if (eventImageUrl != null){
            return eventImageUrl;
        }
        return "https://barowoori-bucket.s3.ap-northeast-2.amazonaws.com/default/FoodFinDefaultImage.png";
    }

    public String calculateDDay(LocalDate targetDate) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), targetDate);

        if (days > 0) {
            return "D-" + days;
        } else if (days == 0) {
            return "D-DAY";
        } else {
            return "D+" + Math.abs(days);
        }
    }
}
