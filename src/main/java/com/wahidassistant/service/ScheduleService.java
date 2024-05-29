package com.wahidassistant.service;

//import com.wahidassistant.component.TravelComponent;
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
public class ScheduleService { // purpose is to be Service class containing methods that manipulate the schedule repository or schedules related to the user.
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final WebScraper webScraper;
    //private final TravelComponent travelComponent;
    private final StringHttpMessageConverter stringHttpMessageConverter;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    } // returns all schedules from Schedule repository. Author Pehr Nortén.

    public Optional<Schedule> getScheduleById(String id){return scheduleRepository.findById(id);} //returns a schedule from the Schedule repository by Id. Author Wahid Hassani.

    public Optional<Schedule> getScheduleByUrl(String url){return scheduleRepository.findScheduleByUrl(url);} //returns a schedule from the Schedule repository by URL. Author Wahid Hassani.

    public Status addOrUpdateSchedule(String url) { // Adds either a new or replaces a existing schedule. Author Pehr Norten & Wahid Hassani.
        Status status = Status.FAILED;
        Schedule newSchedule = webScraper.scrapeSchedule(url); //scrape schedule

        if (newSchedule != null) {
            Optional<Schedule> scheduleOptional = getScheduleByUrl(url); // get existing schedule from repository.
            if (scheduleOptional.isPresent()) { // if it's in the database
                Schedule existingSchedule = scheduleOptional.get(); // get schedule from the optional schedule
                if (checkIfScheduleChanged(newSchedule)) { // sees if same url schedule is changed
                    newSchedule.setId(existingSchedule.getId()); //sets the same id as previous if changed so it's not 2 or more of the same schedule.
                    scheduleRepository.save(newSchedule); //saves over the schedule
                    status = Status.UPDATED;
                } else {
                    existingSchedule.setLastSynced(new Date()); // otherwise if it it's not changed it simply updates lastsynced
                    scheduleRepository.save(existingSchedule); // and saves the previous schedule in the database.
                    status = Status.NO_CHANGE;
                }
            } else {
                scheduleRepository.insert(newSchedule); // adds new schedule
                status = Status.CREATED;
            }
        }
        return status;
    }



    // Todo: See if method can be improved. Not using atm.
    public String getSimilarScheduleId(String url) { // method for returning schedule with similar url
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


    private boolean checkIfScheduleChanged(Schedule updatedSchedule) { // Returns true if schedule has changed. Compares events last updated dates. Author Pehr Nortén, Wahid Hassani & Amer Shikh-Alzor.
        boolean haveChanged = false;
        Optional<Schedule> scheduleOptional = getScheduleByUrl(updatedSchedule.getUrl());
        if (scheduleOptional.isPresent()) {
            Schedule oldSchedule = scheduleOptional.get();
            for (int i = 0; i < updatedSchedule.getEvents().size(); i++) {
                Event updatedEvent = updatedSchedule.getEvents().get(i);
                Event oldEvent = oldSchedule.getEvents().get(i);
                if (oldEvent.getLastUpdated().compareTo(updatedEvent.getLastUpdated()) != 0) { // compares dates returns 0 if they're equal
                    haveChanged = true;
                    break;
                }
            }
        }
        return haveChanged;
    }

    // clean up unused schedules
    // todo: se om nödvändigt att köra varje dag
    public void cleanUpUnusedSchedules() { // method for deleting up schedules not related to users. Author Pehr Nortén.
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

    public Schedule getUsersFullCustomSchedule(User user) { // returns a users full custom schedule from user in database. Combines hidden events and user related schedule. Author Wahid Hassani, Pehr Nortén & Amer Shikh-Alzor.
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

    private boolean isEventsEqual(Event event1, Event event2) { // returns true if all event variables are equal. Author Pehr Nortén, Wahid Hassani & Amer Shikh-Alzor.

        return event1.getCourseName().equals(event2.getCourseName()) &&
                event1.getStartTime().equals(event2.getStartTime()) &&
                event1.getEndTime().equals(event2.getEndTime()) &&
                event1.getDescription().equals(event2.getDescription()) &&
                event1.getTeachers().equals(event2.getTeachers());
    }

    public boolean updateUsersHiddenEventsFromFullCustomSchedule(Schedule newFullCustomSchedule, User user) { // returns true if new full custom schedule hidden events are saved in user. Author Amer Shikh-Alzor, Wahid Hassani & Pehr Nortén.
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

    public void updateUsersCustomEvents(User user) { // updates customEvents that saved in user repository with the new hidden events. Author Wahid Hassani, Amer Shikh-Alzor  & Pehr Nortén.
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


    public void updateAllRelevantUsersCustomEvents(String scheduleIdRef) { // updates customEvents for all users. Author Pehr Nortén, Wahid Hassani & Amer Shikh-Alzor.
        Optional<List<User>> allUsers = userRepository.findUsersByScheduleIdRef(scheduleIdRef); //Man ska inte göra så
        if (allUsers.isPresent()) {
            for (User user : allUsers.get()) {
                updateUsersCustomEvents(user);
            }
        }
    }
/*
    public ArrayList<Event> updateCustomWithTravelEvents(User user , ArrayList<Event> customEvents){ // not implemented yet but will be used for the requirement for getting travelevents in the schedule. Author Wahid Hassani & Adam Khachab.
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

    public ArrayList<Event> getTwoDaysFirstAndLastEvents(ArrayList<Event> customEvents){ // not implemented yet but will be used for the requirement for getting travelevents in the schedule. Author Wahid Hassani & Adam Khachab.
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
