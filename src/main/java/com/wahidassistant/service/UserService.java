package com.wahidassistant.service;

import com.wahidassistant.model.Schedule;
import com.wahidassistant.model.User;
import com.wahidassistant.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public void addSchedule(String username, Schedule schedule) {

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public String getUserScheduleIdRef(String username){ //behöver spara det i databasen som en attribut som heter "scheduleIdRef"
        return userRepository.findScheduleIdRefById(username);
    }

    //todo: skapa metod som säger den ska spara customEvents.
}
