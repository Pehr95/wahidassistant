package com.wahidassistant.repository;

import com.wahidassistant.model.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Schedule in MongoDB
 * Author: Pehr
 */
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    // Finds a schedule by it's URL
    Optional<Schedule> findScheduleByUrl(String url);

    // Finds a schedule by the ending part of it's URL
    Optional<Schedule> findScheduleByUrlEndingWith(String urlEnding);

    // Finds a schedule with events that have date and time between the specific range
    @Query("{'events.eventDateTime': { $gte: ?0, $lt: ?1 }}")
    List<Schedule> findByEventsEventDateTimeBetween(LocalDateTime startOfDay, LocalDateTime startOfNextDay);

    // Deletes a schedule by the ID
    void deleteById(String id);




}
