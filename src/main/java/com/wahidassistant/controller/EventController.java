package com.wahidassistant.controller;

import com.wahidassistant.model.Event;
import com.wahidassistant.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/events")
    public List<Event> fetchAllEvents() {
        return eventService.getAllEvents();
    }
}
