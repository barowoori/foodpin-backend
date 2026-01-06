package com.barowoori.foodpinbackend.event.controller;

import com.barowoori.foodpinbackend.event.command.application.service.EventService;
import com.barowoori.foodpinbackend.event.command.domain.model.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class EventShareController {
    private final EventService eventService;

    public EventShareController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/share/event/{id}")
    public String shareRecipe(@PathVariable String id, Model model) {
//        Event event = eventService.get

        model.addAttribute("title", "test");
        model.addAttribute("description", "test");
        model.addAttribute("image", "https://barowoori-bucket.s3.ap-northeast-2.amazonaws.com/default/f556fd73b220c2079e9b2c14ef100cf7_exif.jpg");
        model.addAttribute("url", "http://localhost:8080/share/event/" + id);

        return "share/event";
    }
}
