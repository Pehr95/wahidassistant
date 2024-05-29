package com.wahidassistant.controller;

import com.wahidassistant.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


// This class is responsible for handling the home page and related routes. Main Author Pehr Nortén. Co-author Wahid and Amer

@Controller
@AllArgsConstructor
public class HomeController {

    public final UserService userService;

    // This method handles the root ("/") route.
    // It checks if the user is logged in and redirects to the appropriate page.
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

    // This method handles the "/settings" route.
    // It checks if the user is logged in and redirects to the settings page or login page accordingly.
    @GetMapping("/settings")
    public String settings(HttpServletRequest request) {
        String username = userService.getUsername(request);
        if (username == null) {
            return "login.html";
        } else {
            return "settings.html";
        }
    }
    // This method handles the "/hide-events" route.
    // It checks if the user is logged in and redirects to the hide-events page or login page accordingly.
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
