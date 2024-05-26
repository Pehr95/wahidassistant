package com.wahidassistant.service;

import com.wahidassistant.component.TravelComponent;
import com.wahidassistant.component.WebScraper;
import com.wahidassistant.model.*;
import com.wahidassistant.repository.ScheduleRepository;
import com.wahidassistant.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;


@AllArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final WebScraper webScraper;
    //private final TravelComponent travelComponent;
    private final StringHttpMessageConverter stringHttpMessageConverter;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getScheduleById(String id){return scheduleRepository.findById(id);}

    public Optional<Schedule> getScheduleByUrl(String url){return scheduleRepository.findScheduleByUrl(url);}

    public Status addOrUpdateSchedule(String url) { //Med Wahid
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



    // Todo: See if method can be improved. Not using atm.
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


    private boolean checkIfScheduleChanged(Schedule updatedSchedule) { //Med Wahid & Amer
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
    // todo: se om nödvändigt att köra varje dag
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

    public Schedule getUsersFullCustomSchedule(User user) { // Med Wahid & Amer
        Optional<Schedule> optionalSchedule = getScheduleById(user.getScheduleIdRef());
        ArrayList<Event> hiddenEvents = user.getHiddenEvents();

        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            if (hiddenEvents == null || hiddenEvents.isEmpty()) {
                return schedule;
            } else {
                for (Event event : schedule.getEvents()) {
                    for (Event hiddenEvent : hiddenEvents) {
                        if (isEventsEqual(event, hiddenEvent)) {
                            event.setHidden(true);
                        }
                    }
                }
                return schedule;
            }
        }
        return null;
    }

    private boolean isEventsEqual(Event event1, Event event2) { //Med Wahid & Amer

        return event1.getCourseName().equals(event2.getCourseName()) &&
                event1.getStartTime().equals(event2.getStartTime()) &&
                event1.getEndTime().equals(event2.getEndTime()) &&
                event1.getDescription().equals(event2.getDescription()) &&
                event1.getTeachers().equals(event2.getTeachers());
    }

    public boolean updateUsersHiddenEventsFromFullCustomSchedule(Schedule newFullCustomSchedule, User user) { // Med Wahid & Amer
        if (user == null) {
            return false;
        }

        ArrayList<Event> hiddenEvents = new ArrayList<>();
        for (Event event : newFullCustomSchedule.getEvents()) {
            if (event.isHidden()) {
                hiddenEvents.add(event);
            }
        }

        user.setHiddenEvents(hiddenEvents);
        userRepository.save(user);
        return true;
    }

    public void updateUsersCustomEvents(User user) { // Med Wahid & Amer
        ArrayList<Event> hiddenEvents = user.getHiddenEvents();
        Optional<Schedule> optionalSchedule = getScheduleById(user.getScheduleIdRef());
        ArrayList<Event> customEvents = new ArrayList<>();


        if (optionalSchedule.isPresent() && hiddenEvents != null) {
            Schedule schedule = optionalSchedule.get();
            for (Event event : schedule.getEvents()) {
                boolean isHidden = false;
                for (Event hiddenEvent : hiddenEvents) {
                    if (isEventsEqual(event, hiddenEvent)) {
                        isHidden = true;
                        break;
                    }
                }
                if (!isHidden) {
                    customEvents.add(event);
                }
            }

            //TODO:
            /*
            if (user.getSettingsData().getPreferredTransportation() != PreferredTransportation.NONE){
                customEvents = updateCustomWithTravelEvents(user, customEvents);
            }

             */

            user.setCustomEvents(customEvents);
            userRepository.save(user);
        }
    }


    public void updateAllRelevantUsersCustomEvents(String scheduleIdRef) { //Med Wahid & Amer
        Optional<List<User>> allUsers = userRepository.findUsersByScheduleIdRef(scheduleIdRef); //Man ska inte göra så
        if (allUsers.isPresent()) {
            for (User user : allUsers.get()) {
                updateUsersCustomEvents(user);
            }
        }
    }
/*
    public ArrayList<Event> updateCustomWithTravelEvents(User user , ArrayList<Event> customEvents){
        ArrayList<Event> customWithTravelEvents = new ArrayList<>();

        SettingsData settingsData = user.getSettingsData();
        String address = settingsData.getAddress();
        String postalcode = settingsData.getPostalCode();
        PreferredTransportation preferredTransportation = settingsData.getPreferredTransportation();

        ArrayList<Event> firstAndLastEventsOfTwoDays = getTwoDaysFirstAndLastEvents(customEvents);

        if (preferredTransportation == PreferredTransportation.BIKE){
            Event DayOnebikeEventBefore = travelComponent.getBikeInfo(address + ", " + postalcode, "Bassänggatan 2, 21119, Malmö", firstAndLastEventsOfTwoDays.get(0).getStartTime(), null);
            Event DayOnebikeEventAfter = travelComponent.getBikeInfo(address + ", " + postalcode, "Bassänggatan 2, 21119, Malmö", null , firstAndLastEventsOfTwoDays.get(1).getEndTime());
            Event DayTwobikeEventBefore = travelComponent.getBikeInfo(address + ", " + postalcode, "Bassänggatan 2, 21119, Malmö", firstAndLastEventsOfTwoDays.get(2).getStartTime(), null);
            Event DayTwobikeEventAfter = travelComponent.getBikeInfo(address + ", " + postalcode, "Bassänggatan 2, 21119, Malmö", null , firstAndLastEventsOfTwoDays.get(3).getEndTime());

            int counter = 0;
            for (int i = 0; i < customEvents.size(); i++){
                for (int j = 0; j < firstAndLastEventsOfTwoDays.size(); j++){
                    if (i==0){
                        customEvents.add(0,DayOnebikeEventBefore);
                        counter++;
                    }
                    else if (isEventsEqual(customEvents.get(i), firstAndLastEventsOfTwoDays.get(j)) && counter == 1){
                        customEvents.add(i, DayOnebikeEventAfter);
                        counter++;
                    }
                    else if (isEventsEqual(customEvents.get(i), firstAndLastEventsOfTwoDays.get(j)) && counter == 2){
                        customEvents.add(i, DayTwobikeEventBefore);
                        counter++;
                    } else if (isEventsEqual(customEvents.get(i), firstAndLastEventsOfTwoDays.get(j)) &&counter == 3) {
                        customEvents.add(i, DayTwobikeEventAfter);
                        counter++;
                        break;
                    }
                }
            }
        } else {
            Event DayOneBusEventBefore = travelComponent.getBusInfo(address + ", " + postalcode, "Bassänggatan 2, 21119, Malmö", firstAndLastEventsOfTwoDays.get(0).getStartTime(), null);
            Event DayOneBusEventAfter = travelComponent.getBusInfo(address + ", " + postalcode, "Bassänggatan 2, 21119, Malmö", null, firstAndLastEventsOfTwoDays.get(1).getEndTime());
            Event DayTwoBusEventBefore = travelComponent.getBusInfo(address + ", " + postalcode, "Bassänggatan 2, 21119, Malmö", firstAndLastEventsOfTwoDays.get(2).getStartTime(), null);
            Event DayTwoBusEventAfter = travelComponent.getBusInfo(address + ", " + postalcode, "Bassänggatan 2, 21119, Malmö", null, firstAndLastEventsOfTwoDays.get(3).getEndTime());

            int counter = 0;
            for (int i = 0; i < customEvents.size(); i++){
                for (int j = 0; j < firstAndLastEventsOfTwoDays.size(); j++){
                    if (i==0){
                        customEvents.add(0,DayOneBusEventBefore);
                        counter++;
                    }
                    else if (isEventsEqual(customEvents.get(i), firstAndLastEventsOfTwoDays.get(j)) && counter == 1){
                        customEvents.add(i, DayOneBusEventAfter);
                        counter++;
                    }
                    else if (isEventsEqual(customEvents.get(i), firstAndLastEventsOfTwoDays.get(j)) && counter == 2){
                        customEvents.add(i, DayTwoBusEventBefore);
                        counter++;
                    } else if (isEventsEqual(customEvents.get(i), firstAndLastEventsOfTwoDays.get(j)) &&counter == 3) {
                        customEvents.add(i, DayTwoBusEventAfter);
                        counter++;
                        break;
                    }
                }
            }
        }
        System.out.println(customEvents);
        return customWithTravelEvents;
    }

 */

    public ArrayList<Event> getTwoDaysFirstAndLastEvents(ArrayList<Event> customEvents){
        ArrayList<Event> fourEventsOfImportance = new ArrayList<>();

        //customEvents.sort(Comparator.comparing(Event ::getStartTime));

        Map<String, ArrayList<Event>> eventsbyDate = new LinkedHashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Event event : customEvents){
            String eventDate = dateFormat.format(event.getStartTime());
            eventsbyDate.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);
        }

        int dayCount = 0;

        for (ArrayList<Event> dailyEvents : eventsbyDate.values()){
            if (dayCount < 2){
                fourEventsOfImportance.add(dailyEvents.get(0));
                fourEventsOfImportance.add(dailyEvents.get(dailyEvents.size()-1));
                dayCount++;
            }else {
                break;
            }
        }
        return fourEventsOfImportance;
    }
}
