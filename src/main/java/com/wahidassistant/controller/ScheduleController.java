package com.wahidassistant.controller;

import com.wahidassistant.config.JwtService;
import com.wahidassistant.model.Event;
import com.wahidassistant.model.Schedule;
import com.wahidassistant.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/schedules")
@AllArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final JwtService jwtService;

    @GetMapping("/test")
    public List<Schedule> fetchAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    public List<Schedule> fetchThisWeekEvents() {
        return scheduleService.getThisWeekEvents();
    }


}