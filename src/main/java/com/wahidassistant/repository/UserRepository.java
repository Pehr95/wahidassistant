package com.wahidassistant.repository;

import com.wahidassistant.model.Event;
import com.wahidassistant.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    @Query(value = "{ 'username' : ?0 }", fields = "{ 'scheduleidref' : 1 }")
    String findScheduleIdRefById(String username);

    @Query (value = "{ 'username' : ?0 }", fields = "{ 'customevents' : 1 }")
    List<Event> findCustomEvents(String username);
    //Todo: fixa att det finns i databasen

    // find all users with a specific schedule id
    Optional<User> findByScheduleIdRef(String id);



}
