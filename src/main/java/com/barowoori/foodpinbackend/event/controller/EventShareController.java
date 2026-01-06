package com.barowoori.foodpinbackend.event.controller;

import com.barowoori.foodpinbackend.event.command.application.service.EventService;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import com.barowoori.foodpinbackend.file.command.domain.service.ImageManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class EventShareController {
    private final EventService eventService;
    private final ImageManager imageManager;

    public EventShareController(EventService eventService, ImageManager imageManager) {
        this.eventService = eventService;
        this.imageManager = imageManager;
    }

    @GetMapping("/share/event/{id}")
    public String shareRecipe(@PathVariable String id, Model model) {
        Event event = eventService.getEventById(id);

        model.addAttribute("title", "푸드핀");
        model.addAttribute("description", event.getName());
        model.addAttribute("image", getImage(event.getEventMainPhotoUrl(imageManager)));
        model.addAttribute("url", "https://dev.barowoori.click/share/event/" + id);

        return "share/event";
    }

    private String getImage(String eventImageUrl){
        if (eventImageUrl != null){
            return eventImageUrl;
        }
        return "https://barowoori-bucket.s3.ap-northeast-2.amazonaws.com/default/FoodFinDefaultImage.png";
    }
}
