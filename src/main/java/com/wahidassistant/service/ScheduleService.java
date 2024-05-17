package com.wahidassistant.service;

import com.wahidassistant.component.WebScraper;
import com.wahidassistant.model.Event;
import com.wahidassistant.model.Schedule;
import com.wahidassistant.model.Status;
import com.wahidassistant.model.User;
import com.wahidassistant.repository.ScheduleRepository;
import com.wahidassistant.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final WebScraper webScraper;
    private final StringHttpMessageConverter stringHttpMessageConverter;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getScheduleById(String id){return scheduleRepository.findById(id);}

    public Optional<Schedule> getScheduleByUrl(String url){return scheduleRepository.findScheduleByUrl(url);}

    // Todo: DELETE THIS METHOD
    public boolean updateSchedule(Schedule updatedSchedule) {
        boolean confirmed = false;
        Optional<Schedule> scheduleOptional = getScheduleByUrl(updatedSchedule.getUrl());
        if (scheduleOptional.isPresent()) {
            Schedule schedule = scheduleOptional.get();
            schedule.setEvents(updatedSchedule.getEvents());
            scheduleRepository.save(schedule);
            confirmed = true;
        }
        return confirmed;
    }

    public Status addOrUpdateSchedule(String url) {
        Status status = Status.FAILED;
        Schedule newSchedule = webScraper.scrapeSchedule(url);

        if (newSchedule != null) {
            Optional<Schedule> scheduleOptional = getScheduleByUrl(url);
            if (scheduleOptional.isPresent()) {
                Schedule existingSchedule = scheduleOptional.get();
                if (checkIfScheduleChanged(newSchedule)) {
                    newSchedule.setId(existingSchedule.getId());
                    scheduleRepository.save(newSchedule);
                    status = Status.UPDATED;
                } else {
                    existingSchedule.setLastSynced(new Date());
                    scheduleRepository.save(existingSchedule);
                    status = Status.NO_CHANGE;
                }
            } else {
                scheduleRepository.insert(newSchedule);
                status = Status.CREATED;
            }
        }
        return status;
    }

    public String getSimilarScheduleId(String url) {
        Optional<Schedule> scheduleOptional = getScheduleByUrl(url);
        if (scheduleOptional.isEmpty()) {
            String newScheduleUrlEnding = url.split("&resurser=")[1];

            List<Schedule> allSchedules = scheduleRepository.findAll();

            if (!allSchedules.isEmpty()) {
                String currentScheduleUrlEnding = "";
                for (Schedule schedule : allSchedules) {
                    currentScheduleUrlEnding = schedule.getUrl().split("&resurser=")[1];
                    if (currentScheduleUrlEnding.equals(newScheduleUrlEnding)) {
                        return schedule.getId();
                    }
                }
            }
        }
        return null;
    }


    // Todo: DELETE THIS METHOD
    public void updateScheduleLastSynced(String id) {
        Optional<Schedule> scheduleOptional = scheduleRepository.findById(id);
        if (scheduleOptional.isPresent()) {
            Schedule schedule = scheduleOptional.get();
            schedule.setLastSynced(new Date());
            scheduleRepository.save(schedule);
        }
    }

    private boolean checkIfScheduleChanged(Schedule updatedSchedule) {
        boolean haveChanged = false;
        Optional<Schedule> scheduleOptional = getScheduleByUrl(updatedSchedule.getUrl());
        if (scheduleOptional.isPresent()) {
            Schedule oldSchedule = scheduleOptional.get();
            for (int i = 0; i < updatedSchedule.getEvents().size(); i++) {
                Event updatedEvent = updatedSchedule.getEvents().get(i);
                Event oldEvent = oldSchedule.getEvents().get(i);
                if (oldEvent.getLastUpdated().compareTo(updatedEvent.getLastUpdated()) != 0) {
                    haveChanged = true;
                    break;
                }
            }
        }
        return haveChanged;
    }

    // clean up unused schedules
    public void cleanUpUnusedSchedules() {
        List<Schedule> scheduleList = scheduleRepository.findAll();
        int counter = 0;
        for (Schedule schedule : scheduleList) {
            if (userRepository.findByScheduleIdRef(schedule.getId()).isEmpty()) {
                scheduleRepository.deleteById(schedule.getId());
                counter++;
            }
        }
        System.out.println("Deleted " + counter + " unused schedules");
    }

}
