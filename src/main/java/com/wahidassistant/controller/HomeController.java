package com.wahidassistant.controller;

import com.wahidassistant.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
@AllArgsConstructor
public class HomeController {

    public final UserService userService;

    @GetMapping("/")
    public String home(HttpServletRequest request) {
        String username = userService.getUsername(request);
        System.out.println(username);
        if (username != null) {
            if (userService.findByUsername(username).isPresent()){
                return "index.html";
            }
        }
        return "login.html";
    }

    @GetMapping("/settings")
    public String settings(HttpServletRequest request) {
        String username = userService.getUsername(request);
        if (username == null) {
            return "login.html";
        } else {
            return "settings.html";
        }
    }

    @GetMapping("/hide-events")
    public String hideEvents(HttpServletRequest request) {
        String username = userService.getUsername(request);
        if (username == null) {
            return "login.html";
        } else {
            return "hide-events.html";
        }
    }




}
