package com.wahidassistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * This class represents an event with course name, start and end time, duration of the lesson
 * rooms, teachers, description, date and isHidden for hiding events.
 * Author: Pehr
 */
@Data
@AllArgsConstructor
public class Event {
    private String courseName;
    private Date startTime;
    private Date endTime;
    private int duration;
    private HashMap<String, String> rooms;
    private ArrayList<String> teachers;
    private String description;
    private Date lastUpdated;
    private boolean isHidden;
}