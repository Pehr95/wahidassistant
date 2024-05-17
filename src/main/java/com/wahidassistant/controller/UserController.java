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
    private final JwtService jwtService;
    private final UserService service;
    private final UserRepository userRepository;

    //private final CustomEvents customEvents;

    @GetMapping("/schedule")
    public List<Schedule> fetchSchedule(HttpServletRequest request) {
        String username = getUsername(request);
        //String scheduleIdRef = service.getUserScheduleIdRef(username);

        Optional<User> optionalUser = service.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return scheduleService.getScheduleById(user.getScheduleIdRef()).map(List::of).orElse(null);
        }

        return null;
    }

    @PostMapping("/settings")
    public ResponseEntity<String> changeSettings(HttpServletRequest request, @RequestBody SettingsData newSettingsData) {
        String username = getUsername(request);
        Optional<User> optionalUser  = service.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        } else {
            User user = optionalUser.get();

            // Todo: Check what settings changed and take different actions based on that

            // Check if URL already exists in database
            Optional<Schedule> optionalExistingSchedule = scheduleService.getScheduleByUrl(newSettingsData.getUrl());
            if (optionalExistingSchedule.isPresent()) {
                Schedule existingSchedule = optionalExistingSchedule.get();
                user.setScheduleIdRef(existingSchedule.getId()); // Update user urlRefId to existing schedule id
            } else {
                Status status = scheduleService.addOrUpdateSchedule(newSettingsData.getUrl());
                if (status == Status.FAILED) {
                    return ResponseEntity.badRequest().body("Invalid Schedule URL");
                } else {
                    // Get schedule by URL and set user scheduleIdRef to the new schedule
                    scheduleService.getScheduleByUrl(newSettingsData.getUrl()).ifPresent(schedule -> user.setScheduleIdRef(schedule.getId()));
                }
            }
            user.setSettingsData(newSettingsData);
            userRepository.save(user);

            return ResponseEntity.ok().body("Settings for " + username + " changed to: " + newSettingsData);
        }
    }

    private String getUsername(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);
        return jwtService.extractUsername(jwt);
    }

    @PostMapping("/hide-events")
    public ResponseEntity<List<Event>> updateCustomEvents(HttpServletRequest request, @RequestBody List<Event> hiddenevents) {
        String username = getUsername(request);
        String scheduleIdRef = service.getUserScheduleIdRef(username);
        //List<Event> customEventList = customEvents.createCustomEvents(hiddenevents,scheduleIdRef);
        //service.createCustomEvents(customEventList); //todo: har inte skapats än
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/hide-events")
    public ResponseEntity<List<Event>> getCustomEvents(HttpServletRequest request) {
        String username = getUsername(request);
        String scheduleIdRef = service.getUserScheduleIdRef(username);

        // Todo: get users schedule with hidden events

        return ResponseEntity.ok().body(null);
    }


}
