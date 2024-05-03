package com.wahidassistant.service;

import com.wahidassistant.model.Schedule;
import com.wahidassistant.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;



@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public void addSchedule(String username, Schedule schedule) {

    }
}
