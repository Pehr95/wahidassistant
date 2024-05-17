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
            if (user.getScheduleIdRef() != null) {
                return scheduleService.getScheduleById(user.getScheduleIdRef()).map(List::of).orElse(null);
            } else {
                return null;
            }
        }

        return null;
    }

    @PostMapping("/settings")
    public ResponseEntity<String> changeSettings(HttpServletRequest request, @RequestBody SettingsData newSettingsData) {
        String username = userService.getUsername(request);
        Optional<User> optionalUser  = userService.findByUsername(username);

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
                String similarScheduleId = scheduleService.getSimilarScheduleId(newSettingsData.getUrl());
                if (similarScheduleId == null) {
                    Status status = scheduleService.addOrUpdateSchedule(newSettingsData.getUrl());
                    if (status == Status.FAILED) {
                        System.out.println("Invalid Schedule URL");
                        return ResponseEntity.badRequest().body("Invalid Schedule URL");
                    } else {
                        // Get new schedule by URL and set user scheduleIdRef to the new schedule
                        scheduleService.getScheduleByUrl(newSettingsData.getUrl()).ifPresent(schedule -> user.setScheduleIdRef(schedule.getId()));
                    }
                } else {
                    user.setScheduleIdRef(similarScheduleId);
                    System.out.println("similar schedule found");
                    System.out.println(scheduleService.getScheduleById(similarScheduleId));
                    scheduleService.getScheduleById(similarScheduleId).ifPresent(schedule -> newSettingsData.setUrl(schedule.getUrl()));
                }
            }
            user.setSettingsData(newSettingsData);
            userRepository.save(user);

            return ResponseEntity.ok().body("Settings for " + username + " changed to: " + newSettingsData);
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
    public ResponseEntity<List<Event>> updateCustomEvents(HttpServletRequest request, @RequestBody List<Event> hiddenevents) {
        String username = userService.getUsername(request);
        String scheduleIdRef = userService.getUserScheduleIdRef(username);
        //List<Event> customEventList = customEvents.createCustomEvents(hiddenevents,scheduleIdRef);
        //service.createCustomEvents(customEventList); //todo: har inte skapats än
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/hide-events")
    public ResponseEntity<List<Event>> getCustomEvents(HttpServletRequest request) {
        String username = userService.getUsername(request);
        String scheduleIdRef = userService.getUserScheduleIdRef(username);

        // Todo: get users schedule with hidden events

        return ResponseEntity.ok().body(null);
    }


}
