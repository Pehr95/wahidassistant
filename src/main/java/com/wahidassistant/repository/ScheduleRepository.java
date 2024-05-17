package com.wahidassistant.repository;

import com.wahidassistant.model.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    Optional<Schedule> findScheduleByUrl(String url);

    Optional<Schedule> findScheduleByUrlEndingWith(String urlEnding);

    @Query("{'events.eventDateTime': { $gte: ?0, $lt: ?1 }}")
    List<Schedule> findByEventsEventDateTimeBetween(LocalDateTime startOfDay, LocalDateTime startOfNextDay);

    void deleteById(String id);




}
