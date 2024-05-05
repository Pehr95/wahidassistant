package com.wahidassistant.repository;

import com.wahidassistant.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    @Query(value = "{ 'username' : ?0 }", fields = "{ 'scheduleIdRef' : 1 }")
    String findScheduleIdRefById(String username);

    //Todo: kunna behöva att spara customEvents
}
