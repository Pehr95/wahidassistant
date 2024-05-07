package com.wahidassistant.controller;

import com.wahidassistant.config.JwtService;
import com.wahidassistant.model.Event;
import com.wahidassistant.repository.UserRepository;
import com.wahidassistant.service.ScheduleService;
import com.wahidassistant.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class UserController {
    private final ScheduleService scheduleService;
    private final JwtService jwtService;
    private final UserService service;
    private final UserRepository userRepository;

    @GetMapping("/schedule")
    public List<String> fetchSchedule(HttpServletRequest request) {

        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);
        System.out.println(jwtService.extractUsername(jwt));

        return scheduleService.getScheduleByUsername();
    }

    @PostMapping("/settings")
    public String changeSettings(HttpServletRequest request) {
        getUsername(request);
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
    public ResponseEntity<List<Event>> createHiddenEvents(@RequestBody List<Event> hiddenevents) {
        String username = userController.getUsername(r)

        String scheduleIdRef = userService.getUserScheduleIdRef()



        userService.createCustomEvents(customEvents); //har inte skapats än
        return new ResponseEntity<>(createdEvents, HttpStatus.CREATED);
    }
}
