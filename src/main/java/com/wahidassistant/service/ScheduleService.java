package com.wahidassistant.service;

import com.wahidassistant.model.Event;
import com.wahidassistant.model.Schedule;
import com.wahidassistant.model.User;
import com.wahidassistant.repository.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> getThisWeekEvents() { return null;
    }

    public List<Event> getAllEvents() {
        return scheduleRepository.findAll().get(0).getEvents();
    }

    public List<String> getScheduleByUsername() {
        return null;
    }


}
