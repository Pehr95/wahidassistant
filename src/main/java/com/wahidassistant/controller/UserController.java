package com.wahidassistant.controller;

import com.wahidassistant.config.JwtService;
import com.wahidassistant.repository.UserRepository;
import com.wahidassistant.service.ScheduleService;
import com.wahidassistant.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    private String getUsername(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);
        return jwtService.extractUsername(jwt);
    }
}
