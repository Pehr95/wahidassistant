package com.wahidassistant.service;

import com.wahidassistant.model.Schedule;
import com.wahidassistant.model.User;
import com.wahidassistant.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public void addSchedule(String username, Schedule schedule) {

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
