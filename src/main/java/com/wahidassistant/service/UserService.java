package com.wahidassistant.service;

import com.wahidassistant.config.JwtService;
import com.wahidassistant.model.Schedule;
import com.wahidassistant.model.SettingsData;
import com.wahidassistant.model.User;
import com.wahidassistant.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// UserService class is responsible for handling operations related to User.
// It is used to interact with the UserRepository and JwtService.
// Author: Wahid, Pehr, Amer
@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    // Method to find a user by their username.

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }
    // Method to get the schedule ID reference of a user by their username.

    public String getUserScheduleIdRef(String username){ //behöver spara det i databasen som en attribut som heter "scheduleIdRef"
        return userRepository.findScheduleIdRefById(username);
    }

    // Method to get the username from the request. It first checks the "Authorization" header,
    // if it's null, it checks the cookies for "auth_token" and extracts the username from it.
    public String getUsername(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    // Check for "auth_token" cookie by name
                    if ("auth_token".equals(cookie.getName())) {
                        return jwtService.extractUsername(cookie.getValue());
                    }
                }
            }
            return null;
        }

        return jwtService.extractUsername(authHeader.substring(7));
    }
    //todo: skapa metod som säger den ska spara customEvents.Efter attributen finns i databasen
}
