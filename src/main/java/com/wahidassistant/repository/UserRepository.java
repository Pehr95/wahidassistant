package com.wahidassistant.repository;

import com.wahidassistant.model.Event;
import com.wahidassistant.model.SettingsData;
import com.wahidassistant.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User in MongoDB
 * Author: Wahid & Pehr
 */
public interface UserRepository extends MongoRepository<User, String> {

    // Finds a user by their username
    Optional<User> findByUsername(String username);

    // Finds the schedule ID reference by the username
    @Query(value = "{ 'username' : ?0 }", fields = "{ 'scheduleidref' : 1 }")
    String findScheduleIdRefById(String username);

    // Finds custom events for a user by their username
    @Query (value = "{ 'username' : ?0 }", fields = "{ 'customevents' : 1 }")
    List<Event> findCustomEvents(String username);
    //Todo: fixa att det finns i databasen

    // find user with a specific schedule id
    Optional<User> findByScheduleIdRef(String id);

    // find all users with a specific schedule id
    Optional<List<User>> findUsersByScheduleIdRef(String id);

    /*
    @Query (value = "{ 'username' : ?0 }" , fields = "{ 'settingsData' : 1 }")
    Optional<SettingsData> findSettingsDataByUsername (String username);

     */

}
