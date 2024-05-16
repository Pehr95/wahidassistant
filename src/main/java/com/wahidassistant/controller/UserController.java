package com.wahidassistant.controller;

import com.wahidassistant.config.JwtService;
import com.wahidassistant.model.Event;
import com.wahidassistant.model.SettingsData;
import com.wahidassistant.model.User;
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
    public List<String> fetchSchedule(HttpServletRequest request) {

        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);
        System.out.println(jwtService.extractUsername(jwt));

        /*
       Wahid & Amer
         */
        //return userRepository.findCustomEvents(getUsername(request));

        return scheduleService.getScheduleByUsername();
    }

    @PostMapping("/settings")
    public String changeSettings(HttpServletRequest request, @RequestBody SettingsData settingsData) {
        String name = getUsername(request);
        Optional<User> user  = service.findByUsername(name);
        User user1 = user.get();
        user1.setSettingsData(settingsData);
        userRepository.save(user1);

        return "Settings changed";
    }

    @GetMapping("/get-username")
    public String showUsername(HttpServletRequest request) {
        return getUsername(request);
    }


    private String getUsername(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);
        System.out.println(request);
        return jwtService.extractUsername(jwt);
    }

    @PostMapping("/hidden-events")
    public ResponseEntity<List<Event>> customEvents(HttpServletRequest request, @RequestBody List<Event> hiddenevents) {
        String username = getUsername(request);
        String scheduleIdRef = service.getUserScheduleIdRef(username);
        //List<Event> customEventList = customEvents.createCustomEvents(hiddenevents,scheduleIdRef);
        //service.createCustomEvents(customEventList); //todo: har inte skapats än
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
