package com.wahidassistant.component;

import com.wahidassistant.model.Event;
import com.wahidassistant.model.Schedule;
import com.wahidassistant.service.ScheduleService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CustomEvents {
    //todo: 1. KLARA! Kunna göra customEvents utifrån hiddenEvents,
    //          todo: adam skickar hiddenevents,
    //           vi har hiddenevents,
    //           med hiddenevents så ska vi skapa customevents skicka det varje gång användaren är inne och när det uppdateras med hiddenevents.
    //      2. KLARA!Kunna skicka customevents och hela schemat,
    //      3. Pehr - Kunna spara hiddenEvents i Usern och customEvents,
    //      4.

    ScheduleService scheduleService;

    public List<Event> createCustomEvents(List<Event> hiddenEvents, String id){
        List<Event> customEvents = new ArrayList<>();
        Optional<Schedule> schedule = scheduleService.getScheduleById(id);
        List<Event> allEvents = schedule.get().getEvents();

        for(int i = 0; i < allEvents.size(); i++){
            for (int j = 0; j < hiddenEvents.size(); j++){
                if(!Objects.equals(hiddenEvents.get(j).getCourseName(), allEvents.get(i).getCourseName()) && hiddenEvents.get(j).getStartTime() != allEvents.get(i).getStartTime());
                customEvents.add(allEvents.get(i));
            }
        }
        return customEvents;
    }
}
