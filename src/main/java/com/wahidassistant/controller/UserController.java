package com.wahidassistant.controller;

import com.wahidassistant.config.JwtService;
import com.wahidassistant.model.*;
import com.wahidassistant.repository.UserRepository;
import com.wahidassistant.service.ScheduleService;
import com.wahidassistant.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class UserController {
    private final ScheduleService scheduleService;
    private final UserService userService;
    private final UserRepository userRepository;

    //private final CustomEvents customEvents;

    @GetMapping("/schedule")
    public List<Schedule> fetchSchedule(HttpServletRequest request) {
        String username = userService.getUsername(request);
        //String scheduleIdRef = service.getUserScheduleIdRef(username);

        Optional<User> optionalUser = userService.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getCustomEvents() != null) {
                Optional<Schedule> optionalSchedule = scheduleService.getScheduleById(user.getScheduleIdRef());
                if (optionalSchedule.isPresent()) {
                    Schedule schedule = optionalSchedule.get();
                    schedule.setEvents(user.getCustomEvents());
                    return List.of(schedule);
                } else {
                    return null;
                }

            } else {
                return scheduleService.getScheduleById(user.getScheduleIdRef()).map(List::of).orElse(null);
            }
        }

        return null;
    }

    @PostMapping("/settings")
    public ResponseEntity<SettingsData> changeSettings(HttpServletRequest request, @RequestBody SettingsData newSettingsData) {
        String username = userService.getUsername(request);
        Optional<User> optionalUser  = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        } else {
            User user = optionalUser.get();

            // Todo: Check what settings changed and take different actions based on that

            // Check if exact URL already exists in database
            Optional<Schedule> optionalExistingSchedule = scheduleService.getScheduleByUrl(newSettingsData.getUrl());
            if (optionalExistingSchedule.isPresent()) {
                Schedule existingSchedule = optionalExistingSchedule.get();
                user.setScheduleIdRef(existingSchedule.getId()); // Update user urlRefId to existing schedule id
            } else {
                Status status = scheduleService.addOrUpdateSchedule(newSettingsData.getUrl());
                if (status == Status.FAILED) {
                    System.out.println("Invalid Schedule URL");
                    return ResponseEntity.badRequest().body(null);
                } else {
                    // Get new schedule by URL and set user scheduleIdRef to the new schedule
                    scheduleService.getScheduleByUrl(newSettingsData.getUrl()).ifPresent(schedule -> user.setScheduleIdRef(schedule.getId()));
                    scheduleService.updateUsersCustomEvents(user);
                }
            }
            user.setSettingsData(newSettingsData);
            userRepository.save(user);
            scheduleService.updateUsersCustomEvents(user);

            return ResponseEntity.ok().body(newSettingsData);
            //return ResponseEntity.ok().body("Settings for " + username + " changed to: " + newSettingsData);
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsData> getSettings(HttpServletRequest request) {
        String username = userService.getUsername(request);
        Optional<User> optionalUser  = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok().body(optionalUser.get().getSettingsData());
    }

    @PostMapping("/hide-events")
    public ResponseEntity<List<Event>> updateCustomEvents(HttpServletRequest request, @RequestBody Schedule newFullCustomSchedule) {
        String username = userService.getUsername(request);
        Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = optionalUser.get();


        if (scheduleService.updateUsersHiddenEventsFromFullCustomSchedule(newFullCustomSchedule, user)) {
            // Todo: Implement logic to update users custom events
            scheduleService.updateUsersCustomEvents(user);
            return ResponseEntity.ok().body(newFullCustomSchedule.getEvents());
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/hide-events")
    public ResponseEntity<Schedule> getCustomEvents(HttpServletRequest request) {
        String username = userService.getUsername(request);
        Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok().body(scheduleService.getUsersFullCustomSchedule(optionalUser.get()));
    }


}
